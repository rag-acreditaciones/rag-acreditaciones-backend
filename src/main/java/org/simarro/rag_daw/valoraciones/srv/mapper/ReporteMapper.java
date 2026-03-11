package org.simarro.rag_daw.valoraciones.srv.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.simarro.rag_daw.valoraciones.model.db.ReporteRespuestaDb;
import org.simarro.rag_daw.valoraciones.model.dto.ReporteDTO;

@Mapper(componentModel = "spring")
public interface ReporteMapper {

    @Mapping(target = "motivo", expression = "java(entity.getMotivo().name())")
    @Mapping(target = "estado", expression = "java(entity.getEstado().name())")
    ReporteDTO toDTO(ReporteRespuestaDb entity);

}