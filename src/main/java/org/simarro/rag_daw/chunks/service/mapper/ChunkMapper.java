package org.simarro.rag_daw.chunks.service.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.simarro.rag_daw.chunks.model.DB.DocumentoChunkDb;
import org.simarro.rag_daw.chunks.model.DTO.ChunkBusquedaDTO;
import org.simarro.rag_daw.chunks.model.DTO.ChunkDetailDTO;
import org.simarro.rag_daw.chunks.model.DTO.ChunkResponseDTO;
import org.simarro.rag_daw.documentos.service.mapper.DocumentoMapper;
import org.simarro.rag_daw.model.dto.FiltroBusqueda;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.springframework.data.domain.Page;

@Mapper(uses = DocumentoMapper.class)
public interface ChunkMapper {

        ChunkMapper INSTANCE = Mappers.getMapper(ChunkMapper.class);

        // DC -- CBDTO
        @Mapping(target = "documento", source = "documentos")
        @Mapping(target = "texto", source = "textoCompleto")
        ChunkBusquedaDTO documentoChunkToChunkBusquedaDTO(DocumentoChunkDb docChunk);

        DocumentoChunkDb chunkBusquedaDTOToDocumentoChunk(ChunkBusquedaDTO chunkBusqueda);

        static PaginaResponse<ChunkBusquedaDTO> pageToPaginaResponseChunkBusquedaDTO(
                        Page<DocumentoChunkDb> page,
                        List<FiltroBusqueda> filtros,
                        List<String> ordenaciones) {

                return new PaginaResponse<>(
                                page.getNumber(),
                                page.getSize(),
                                page.getTotalElements(),
                                page.getTotalPages(),
                                page.getContent().stream().map(ChunkMapper.INSTANCE::documentoChunkToChunkBusquedaDTO)
                                                .collect(Collectors.toList()),
                                filtros,
                                ordenaciones);
        }

        // DC--> CDDTO
        ChunkDetailDTO documentoChunkToChunkDetailDTO(DocumentoChunkDb docChunk);

        @Mapping(target = "documentos", ignore = true)
        @Mapping(target = "vectorStoreId", ignore = true)
        DocumentoChunkDb chunkDetailDTOToDocumentoChunk(ChunkDetailDTO chunkDetail);

        static PaginaResponse<ChunkDetailDTO> pageToPaginaResponseChunkDetailDTO(
                        Page<DocumentoChunkDb> page,
                        List<FiltroBusqueda> filtros,
                        List<String> ordenaciones) {

                return new PaginaResponse<>(
                                page.getNumber(),
                                page.getSize(),
                                page.getTotalElements(),
                                page.getTotalPages(),
                                page.getContent().stream().map(ChunkMapper.INSTANCE::documentoChunkToChunkDetailDTO)
                                                .collect(Collectors.toList()),
                                filtros,
                                ordenaciones);
        }
        // DC --> CRDTO

        @Mapping(target = "truncado100chars", expression = "java(docChunk.getTextoCompleto().substring(0, Math.min(100, docChunk.getTextoCompleto().length())))")
        ChunkResponseDTO documentoChunkToChunkResponseDTO(DocumentoChunkDb docChunk);

        @Mapping(target = "textoCompleto", ignore = true)
        @Mapping(target = "documentos", ignore = true)
        @Mapping(target = "vectorStoreId", ignore = true)
        DocumentoChunkDb chunkResponseDTOToDocumentoChunk(ChunkResponseDTO chunkResponse);

        static PaginaResponse<ChunkResponseDTO> pageToPaginaResponseChunkResponseDTO(
                        Page<DocumentoChunkDb> page,
                        List<FiltroBusqueda> filtros,
                        List<String> ordenaciones) {

                return new PaginaResponse<>(
                                page.getNumber(),
                                page.getSize(),
                                page.getTotalElements(),
                                page.getTotalPages(),
                                page.getContent().stream().map(ChunkMapper.INSTANCE::documentoChunkToChunkResponseDTO)
                                                .collect(Collectors.toList()),
                                filtros,
                                ordenaciones);
        }

}
