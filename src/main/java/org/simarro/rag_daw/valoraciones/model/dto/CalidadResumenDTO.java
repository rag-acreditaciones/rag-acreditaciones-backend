package org.simarro.rag_daw.valoraciones.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Resumen global de métricas de calidad")
public class CalidadResumenDTO {
    
    @Schema(example = "150", description = "Total de valoraciones realizadas")
    private long totalValoraciones;
    
    @Schema(example = "120", description = "Total de valoraciones positivas")
    private long positivas;
    
    @Schema(example = "30", description = "Total de valoraciones negativas")
    private long negativas;
    
    @Schema(example = "0.8", description = "Ratio de calidad (positivas / total)")
    private double ratioCalidad;
    
    @Schema(example = "25", description = "Total de reportes creados")
    private long totalReportes;
    
    @Schema(example = "10", description = "Reportes pendientes de revisión")
    private long reportesPendientes;
}