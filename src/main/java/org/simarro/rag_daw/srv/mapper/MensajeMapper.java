package org.simarro.rag_daw.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.simarro.rag_daw.model.db.MensajeDb;
import org.simarro.rag_daw.model.dto.Mensaje;
import org.simarro.rag_daw.model.dto.MensajeDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MensajeMapper {

    // Entity ↔ DTO completo
    MensajeDTO toDTO(MensajeDb entity);
    MensajeDb toEntity(MensajeDTO dto);

    List<MensajeDTO> toDTOList(List<MensajeDb> entities);
    List<MensajeDb> toEntityList(List<MensajeDTO> dtos);

    // Mapper para el DTO simple (solo contenido)
    @Mapping(target = "mensaje", source = "contenido")
    Mensaje toSimple(MensajeDb entity);

    List<Mensaje> toSimpleList(List<MensajeDb> entities);
}