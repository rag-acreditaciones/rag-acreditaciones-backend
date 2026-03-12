package org.simarro.rag_daw.chunks.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.simarro.rag_daw.chunks.model.DB.DocumentoChunkDb;
import org.simarro.rag_daw.chunks.model.DTO.ChunkBusquedaDTO;
import org.simarro.rag_daw.chunks.model.DTO.ChunkDetailDTO;
import org.simarro.rag_daw.chunks.model.DTO.ChunkResponseDTO;
import org.simarro.rag_daw.chunks.model.DTO.ChunkStatsDTO;
import org.simarro.rag_daw.chunks.model.ENUMS.EstadoChunk;
import org.simarro.rag_daw.chunks.model.interfaces.DocumentoChunkStatsInterface;
import org.simarro.rag_daw.chunks.repository.ChunkRepository;
import org.simarro.rag_daw.chunks.service.ChunkService;
import org.simarro.rag_daw.chunks.service.mapper.ChunkMapper;
import org.simarro.rag_daw.config.RagClientBundle;
import org.simarro.rag_daw.config.RagDynamicConfig;
import org.simarro.rag_daw.documentos.model.dto.DocumentoResponseDTO;
import org.simarro.rag_daw.documentos.repository.DocumentoRepository;
import org.simarro.rag_daw.documentos.service.mapper.DocumentoMapper;
import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.simarro.rag_daw.helper.FiltroBusquedaFactory;
import org.simarro.rag_daw.helper.PaginationFactory;
import org.simarro.rag_daw.helper.PeticionListadoFiltradoConverter;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.model.dto.PeticionListadoFiltrado;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

@Service
public class ChunkServiceImpl implements ChunkService {

    private final FiltroBusquedaFactory filtroBusquedaFactory;
    @Autowired
    ChunkRepository chunkRepository;
    @Autowired
    DocumentoRepository documentoRepository;
    @Autowired
    PeticionListadoFiltradoConverter peticionConverter;
    @Autowired
    PaginationFactory paginationFactory;
    @Autowired
    RagDynamicConfig ragDynamicConfig;
    @Autowired
    DocumentoMapper documentoMapper;

    ChunkServiceImpl(FiltroBusquedaFactory filtroBusquedaFactory) {
        this.filtroBusquedaFactory = filtroBusquedaFactory;
    }

    @Override
    public ChunkDetailDTO findById(Long id) {
        Optional<DocumentoChunkDb> documento = chunkRepository.findById(id);

        if (documento.isPresent()) {
            return ChunkMapper.INSTANCE.documentoChunkToChunkDetailDTO(documento.get());
        }
        // TODO: ERROR_CHUNK_NOT_FOUND
        return null;
    }

    @Override
    public PaginaResponse<ChunkDetailDTO> findByDocumentoId(Long docId, String estado, int page, int size,
            String[] sort)
            throws FiltroException {
        try {
            String[] order = { "orden", "asc" };
            PeticionListadoFiltrado peticion = peticionConverter.convertFromParams(new String[] {}, page, size, order);
            Pageable pageable = paginationFactory.createPageable(peticion);
            Page<DocumentoChunkDb> pageChunk;

            if (estado != null && !estado.isBlank()) {
                pageChunk = chunkRepository.findByDocumentosIdAndEstado(docId,
                        EstadoChunk.valueOf(estado.toUpperCase()), pageable);
            } else {
                pageChunk = chunkRepository.findByDocumentosId(docId, pageable);
            }

            return ChunkMapper.pageToPaginaResponseChunkDetailDTO(pageChunk, peticion.getListaFiltros(),
                    peticion.getSort());

        } catch (JpaSystemException e) {
            String cause = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
            throw new FiltroException("BAD_OPERATOR_FILTER",
                    "No se puede aplicar esta operación sobre el atributo por incompatibilidad de tipos",
                    cause);
        } catch (PropertyReferenceException e) {
            throw new FiltroException("BAD_ATTRIBUTE_ORDER",
                    "El atributo de ordenación '" + e.getPropertyName() + "' no existe en "
                            + e.getType().getType().getSimpleName(),
                    e.getMessage());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new FiltroException("BAD_ATTRIBUTE_FILTER",
                    "El atributo de filtrado no existe en la entidad",
                    e.getMessage());
        }
    }

    @Override
    public PaginaResponse<ChunkBusquedaDTO> buscarPorTexto(String texto, Long seccionId, int page, int size)
            throws FiltroException {
        try {

            String[] order = { "id", "asc" };
            PeticionListadoFiltrado peticion = peticionConverter.convertFromParams(new String[] {}, page, size, order);
            Pageable pageable = paginationFactory.createPageable(peticion);
            Page<DocumentoChunkDb> pageChunk;

            if (seccionId != null) {
                pageChunk = chunkRepository.buscarPorTextoYSeccion(texto, seccionId, pageable);
            } else {
                pageChunk = chunkRepository.buscarPorTexto(texto, pageable);
            }

            PaginaResponse<ChunkBusquedaDTO> response = ChunkMapper.pageToPaginaResponseChunkBusquedaDTO(
                    pageChunk, peticion.getListaFiltros(), peticion.getSort());

            String textoMin = texto.toLowerCase();
            for (ChunkBusquedaDTO chunk : response.getContent()) {
                List<Integer> posiciones = new ArrayList<>();
                int index = -1;
                while ((index = chunk.getTexto().indexOf(textoMin, index + 1)) != -1)
                    posiciones.add(index);
                chunk.setPosicionesBusquedaTextual(posiciones);
            }
            // TODO COMPROVAR QUE TORNA LES POSSICIONS SEMPRE.
            return response;

        } catch (JpaSystemException e) {
            String cause = "";
            if (e.getRootCause() != null) {
                if (e.getCause().getMessage() != null) {
                    cause = e.getRootCause().getMessage();
                }
            }
            throw new FiltroException("BAD_OPERATOR_FILTER",
                    "Error: No es pot realitzar aquesta operació sobre l'atribut pel tipus de dada",
                    e.getMessage() + ":" + cause);
        } catch (PropertyReferenceException e) {
            throw new FiltroException("BAD_ATTRIBUTE_ORDER",
                    "Error: No existeix el nom de l'atribut d'ordenació a la taula", e.getMessage());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new FiltroException("BAD_ATTRIBUTE_FILTER", "Error: Possiblement no existeix l'atributo a la taula",
                    e.getMessage());
        }
    }

    @Override
    public List<ChunkBusquedaDTO> busquedaSemantica(Map<String, Object> body)
            throws ResourceNotFoundException {

        // if (!body.containsKey("consulta") || body.get("consulta") == null)
        // throw new DataValidationException("PARAMETERS_NOT_FOUND",
        // "El parámetro de búsqueda no ha sido introducido");

        String consulta = (String) body.get("consulta");
        int topK = (body.containsKey("topK") && body.get("topK") != null)
                ? Integer.parseInt(body.get("topK").toString())
                : 4;

        try {
            RagClientBundle bundle = ragDynamicConfig.crearClienteRag(null);
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(consulta)
                    .topK(topK)
                    .similarityThreshold(0.1)
                    .build();

            return bundle.vectorStore().similaritySearch(searchRequest)
                    .stream()
                    .map(d -> {
                        UUID uuid = UUID.fromString(d.getId());
                        DocumentoChunkDb dchunk = chunkRepository.findByVectorStoreId(uuid)
                                .orElseThrow(() -> new ResourceNotFoundException("CHUNK_NOT_FOUND"));
                        ChunkBusquedaDTO cbDTO = ChunkMapper.INSTANCE.documentoChunkToChunkBusquedaDTO(dchunk);
                        cbDTO.setSimilitud(d.getScore());
                        return cbDTO;
                    })
                    .collect(Collectors.toList());
        } catch (ClassCastException e) {
            throw new ResourceNotFoundException("BAD_TYPE_ERROR", e.getCause());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error en búsqueda semántica: " + e.getMessage(), e);
        }
    }

    /**
     * @apiNote Retorna un chunk
     * @param String id
     * @throws
     * @return ChunkDetailDTO
     */
    @Override
    public ChunkStatsDTO getChunkStats(Long docId) {
        DocumentoChunkStatsInterface statsInterface = chunkRepository.chunkStats(docId);
        DocumentoResponseDTO doc = documentoMapper.toResponseDTO(documentoRepository.findById(docId)
                .orElseThrow(() -> new ResourceNotFoundException()));
        return new ChunkStatsDTO(
                doc,
                statsInterface.getNumeroChunks(),
                statsInterface.getEstadoPendiente(),
                statsInterface.getEstadoRevisado(),
                statsInterface.getEstadoDescartado(),
                statsInterface.getLongitudMedia().longValue(),
                statsInterface.getLongitudMax(),
                statsInterface.getLongitudMin(),
                statsInterface.getTotalTokens());
    }

    @Override
    public ChunkResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        DocumentoChunkDb documento = chunkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CHUNK_NOT_FOUND"));
        documento.setEstado(EstadoChunk.valueOf(nuevoEstado));
        chunkRepository.save(documento);
        return ChunkMapper.INSTANCE.documentoChunkToChunkResponseDTO(documento);
    }

}
