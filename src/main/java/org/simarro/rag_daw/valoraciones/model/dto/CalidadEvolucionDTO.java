package org.simarro.rag_daw.valoraciones.model.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Datos de evolución temporal de calidad")
public class CalidadEvolucionDTO {
    
    @Schema(example = "2026-03-11", description = "Fecha del período (según agrupación)")
    private LocalDate fecha;
    
    @Schema(example = "15", description = "Valoraciones positivas en el período")
    private Long positivas;
    
    @Schema(example = "3", description = "Valoraciones negativas en el período")
    private Long negativas;
    
    @Schema(example = "0.833", description = "Ratio de calidad del período")
    private Double ratio;
    
    @Schema(example = "2", description = "Total de reportes en el período")
    private Long totalReportes;
}