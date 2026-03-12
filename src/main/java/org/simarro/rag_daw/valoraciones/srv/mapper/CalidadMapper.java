package org.simarro.rag_daw.valoraciones.srv.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadEvolucionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadPorSeccionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.CalidadResumenDTO;
import org.simarro.rag_daw.valoraciones.model.dto.TopRespuestaDTO;

@Mapper(componentModel = "spring")
public interface CalidadMapper {
    
    CalidadMapper INSTANCE = Mappers.getMapper(CalidadMapper.class);

    // Métodos estáticos para conversiones complejas
    static List<CalidadEvolucionDTO> toCalidadEvolucionList(List<Object[]> results) {
        return results.stream()
                .map(result -> new CalidadEvolucionDTO(
                        ((java.sql.Date) result[0]).toLocalDate(),
                        (Long) result[1],
                        (Long) result[2],
                        calculateRatio((Long) result[1], (Long) result[2]),
                        0L))
                .collect(Collectors.toList());
    }

    static Double calculateRatio(Long positivas, Long negativas) {
        long total = positivas + negativas;
        return total == 0 ? 0.0 : (double) positivas / total;
    }

    static CalidadResumenDTO toCalidadResumen(
            long totalValoraciones, long positivas, long negativas, 
            long totalReportes, long reportesPendientes) {
        double ratio = totalValoraciones == 0 ? 0.0 : (double) positivas / totalValoraciones;
        return new CalidadResumenDTO(
                totalValoraciones, positivas, negativas, 
                ratio, totalReportes, reportesPendientes);
    }
}