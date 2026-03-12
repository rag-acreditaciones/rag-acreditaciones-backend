package org.simarro.rag_daw.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.simarro.rag_daw.model.db.ConversacionDb;
import org.simarro.rag_daw.model.dto.ConversacionCreateDTO;
import org.simarro.rag_daw.model.dto.ConversacionDetailDTO;
import org.simarro.rag_daw.model.dto.ConversacionResponseDTO;

@Mapper(componentModel = "spring")
public interface ConversacionMapper {

    // Crear entidad desde DTO de creación
    ConversacionDb toEntity(ConversacionCreateDTO dto);

    // Convertir entidad a DTO de detalle
    @Mapping(target = "listaMensajes", ignore = true)
    ConversacionDetailDTO toDetailDTO(ConversacionDb entity);

    // Convertir entidad a DTO de respuesta (lista)
    @Mapping(target = "ultimoMensaje", ignore = true)
    ConversacionResponseDTO toResponseDTO(ConversacionDb entity);
}