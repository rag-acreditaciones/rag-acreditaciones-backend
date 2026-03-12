package org.simarro.rag_daw.rag.srv.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.simarro.rag_daw.chunks.model.DB.DocumentoChunkDb;
import org.simarro.rag_daw.chunks.model.ENUMS.EstadoChunk;
import org.simarro.rag_daw.chunks.repository.ChunkRepository;
import org.simarro.rag_daw.config.RagClientBundle;
import org.simarro.rag_daw.config.RagDynamicConfig;
import org.simarro.rag_daw.documentos.model.db.DocumentoDb;
import org.simarro.rag_daw.documentos.repository.DocumentoRepository;
import org.simarro.rag_daw.rag.model.db.RagConfiguracionDb;
import org.simarro.rag_daw.rag.model.dto.DocumentoMetadataDTO;
import org.simarro.rag_daw.rag.model.dto.IngestaResultDTO;
import org.simarro.rag_daw.rag.srv.IngestaService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class IngestaServiceImpl implements IngestaService {

    private final RagDynamicConfig ragDynamicConfig;
    private final ChunkRepository chunkRepository;
    private final DocumentoRepository documentoRepository;

    public IngestaServiceImpl(RagDynamicConfig ragDynamicConfig,
            ChunkRepository chunkRepository,
            DocumentoRepository documentoRepository) {
        this.ragDynamicConfig = ragDynamicConfig;
        this.chunkRepository = chunkRepository;
        this.documentoRepository = documentoRepository;
    }

    @Override
    public IngestaResultDTO procesarPdf(byte[] pdfBytes,
            DocumentoMetadataDTO metadata) {
        return procesarPdf(pdfBytes, metadata, null);
    }

    @Override
    public IngestaResultDTO procesarPdf(byte[] pdfBytes,
            DocumentoMetadataDTO metadata,
            Long ragConfiguracionId) throws EntityNotFoundException{
        // 1. Obtener el bundle de clientes para este RAG
        RagClientBundle bundle = ragDynamicConfig.crearClienteRag(ragConfiguracionId);
        RagConfiguracionDb config = bundle.configuracion();

        // --- INICIO MODIFICACIÓN claude.ai ---
        // Validar magic bytes antes de pasarlo al lector de PDF (PDFBox lanza una
        // excepción poco descriptiva si el fichero está corrupto o truncado)
        if (pdfBytes == null || pdfBytes.length < 5
                || !new String(pdfBytes, 0, 5).startsWith("%PDF-")) {
            throw new IllegalArgumentException(
                    "El fichero no es un PDF valido o está corrupto (firma incorrecta o tamaño insuficiente)");
        }
        // --- FIN MODIFICACIÓN claude.ai ---

        // 2. Leer PDF (una página = un documento)
        Resource pdfResource = new ByteArrayResource(pdfBytes);
        var lectorPdf = new PagePdfDocumentReader(pdfResource,
                PdfDocumentReaderConfig.builder()
                        .withPagesPerDocument(1)
                        .build());

        // 3. Dividir en chunks por tokens
        var splitter = new TokenTextSplitter(800, 350, 5, 200, true);
        List<Document> chunks = splitter.apply(lectorPdf.get());

        // --- INICIO MODIFICACIÓN ---
        // Validar que el documento existe en BD ANTES de tocar el vector store.
        // Si se hace después (orden original) y falla, quedan chunks huérfanos en
        // pgvector que la búsqueda semántica devuelve pero no puede correlacionar
        // con documento_chunk, provocando CHUNK_NOT_FOUND.
        // DocumentoDb documento = documentoRepository.findById(metadata.documentoId())  // línea original movida
        //         .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado: " + metadata.documentoId()));
        DocumentoDb documento = documentoRepository.findById(metadata.documentoId())
                .orElseThrow(() -> new EntityNotFoundException("Documento no encontrado: " + metadata.documentoId()));
        // --- FIN MODIFICACIÓN ---

        // 4. Enriquecer metadata de cada chunk
        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> meta = chunks.get(i).getMetadata();
            meta.put("documentoId", metadata.documentoId());
            meta.put("nombreFichero", metadata.nombreFichero());
            meta.put("seccionTematica", metadata.seccionTematica());
            meta.put("subidoPor", metadata.subidoPor());
            meta.put("ordenChunk", i + 1);
            meta.put("ragConfiguracionId", config.getId());
            meta.put("ragNombre", config.getNombre());
            meta.put("modeloEmbedding", config.getModeloEmbedding().getModeloId());
            meta.put("dimensiones", config.getModeloEmbedding().getDimensiones());
        }

        // 5. Almacenar (genera embeddings + insert en tabla vectorial del RAG)
        bundle.vectorStore().add(chunks);

        List<DocumentoChunkDb> chunkEntities = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            Document doc = chunks.get(i);
            DocumentoChunkDb entity = new DocumentoChunkDb();
            entity.setOrden(i + 1);
            entity.setTextoCompleto(doc.getText());
            entity.setNumTokens(doc.getText().length() / 4);
            entity.setVectorStoreId(UUID.fromString(doc.getId()));
            entity.setDocumentos(documento);
            entity.setEstado(EstadoChunk.PENDIENTE);
            chunkEntities.add(entity);
        }
        chunkRepository.saveAll(chunkEntities);

        // 6. Extraer UUIDs generados
        List<String> ids = chunks.stream()
                .map(Document::getId)
                .collect(Collectors.toList());

        return new IngestaResultDTO(
                chunks.size(), ids,
                chunks.stream().mapToInt(c -> c.getText().length() / 4).sum(),
                config.getId(), config.getNombre(),
                config.getModeloEmbedding().getModeloId(),
                config.getModeloEmbedding().getDimensiones());
    }
}
