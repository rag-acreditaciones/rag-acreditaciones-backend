package org.simarro.rag_daw.rag.srv.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.simarro.rag_daw.config.RagClientBundle;
import org.simarro.rag_daw.config.RagDynamicConfig;
import org.simarro.rag_daw.rag.model.db.RagConfiguracionDb;
import org.simarro.rag_daw.rag.model.dto.DocumentoMetadataDTO;
import org.simarro.rag_daw.rag.model.dto.IngestaResultDTO;
import org.simarro.rag_daw.rag.srv.IngestaService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
public class IngestaServiceImpl implements IngestaService {

    private final RagDynamicConfig ragDynamicConfig;

    public IngestaServiceImpl(RagDynamicConfig ragDynamicConfig) {
        this.ragDynamicConfig = ragDynamicConfig;
    }

    @Override
    public IngestaResultDTO procesarPdf(byte[] pdfBytes,
                                         DocumentoMetadataDTO metadata) {
        return procesarPdf(pdfBytes, metadata, null);
    }

    @Override
    public IngestaResultDTO procesarPdf(byte[] pdfBytes,
                                         DocumentoMetadataDTO metadata,
                                         Long ragConfiguracionId) {
        // 1. Obtener el bundle de clientes para este RAG
        RagClientBundle bundle = ragDynamicConfig.crearClienteRag(ragConfiguracionId);
        RagConfiguracionDb config = bundle.configuracion();

        // 2. Leer PDF (una página = un documento)
        Resource pdfResource = new ByteArrayResource(pdfBytes);
        var lectorPdf = new PagePdfDocumentReader(pdfResource,
            PdfDocumentReaderConfig.builder()
                .withPagesPerDocument(1)
                .build());

        // 3. Dividir en chunks por tokens
        var splitter = new TokenTextSplitter(800, 350, 5, 200, true);
        List<Document> chunks = splitter.apply(lectorPdf.get());

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
