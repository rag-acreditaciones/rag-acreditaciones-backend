package org.simarro.rag_daw.documentos.service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.simarro.rag_daw.documentos.model.db.DocumentoDb;
import org.simarro.rag_daw.documentos.model.db.SeccionTematicaDb;
import org.simarro.rag_daw.documentos.model.dto.DocumentoDetailDTO;
import org.simarro.rag_daw.documentos.model.dto.DocumentoResponseDTO;
import org.simarro.rag_daw.documentos.model.dto.SeccionTematicaDTO;
import org.simarro.rag_daw.model.dto.FiltroBusqueda;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface DocumentoMapper {

    @Mapping(source = "seccionTematica.id", target = "seccionTematicaId")
    @Mapping(source = "seccionTematica.nombre", target = "seccionTematicaNombre")
    DocumentoResponseDTO toResponseDTO(DocumentoDb db);

    List<DocumentoResponseDTO> toResponseDTOList(List<DocumentoDb> list);

    @Mapping(source = "seccionTematica.id", target = "seccionTematicaId")
    @Mapping(source = "seccionTematica.nombre", target = "seccionTematicaNombre")
    DocumentoDetailDTO toDetailDTO(DocumentoDb db);

    SeccionTematicaDTO toSeccionDTO(SeccionTematicaDb db);

    List<SeccionTematicaDTO> toSeccionDTOList(List<SeccionTematicaDb> list);

    default PaginaResponse<DocumentoResponseDTO> pageToPaginaResponse(
            Page<DocumentoDb> page,
            List<FiltroBusqueda> filtros,
            List<String> ordenaciones) {
        return new PaginaResponse<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                toResponseDTOList(page.getContent()),
                filtros,
                ordenaciones);
    }
}
