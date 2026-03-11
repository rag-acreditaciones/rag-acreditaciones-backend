package org.simarro.rag_daw.valoraciones.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.simarro.rag_daw.valoraciones.model.db.ValoracionDb;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionDTO;

@Mapper(componentModel = "spring")
public interface ValoracionMapper {

    @Mapping(target = "valoracion", expression = "java(entity.getValoracion().name())")
    ValoracionDTO toDTO(ValoracionDb entity);
}