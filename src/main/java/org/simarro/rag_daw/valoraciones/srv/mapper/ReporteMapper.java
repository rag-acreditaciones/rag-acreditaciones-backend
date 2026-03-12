package org.simarro.rag_daw.valoraciones.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.simarro.rag_daw.valoraciones.model.db.ReporteRespuestaDb;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteDTO;

@Mapper(componentModel = "spring")
public interface ReporteMapper {
    
    ReporteMapper INSTANCE = Mappers.getMapper(ReporteMapper.class);

    @Mapping(target = "motivo", expression = "java(entity.getMotivo().name())")
    @Mapping(target = "estado", expression = "java(entity.getEstado().name())")
    @Mapping(target = "usuarioEmail", ignore = true) 
    ReporteDTO toDTO(ReporteRespuestaDb entity);
}