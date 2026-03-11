package org.simarro.rag_daw.valoraciones.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Métricas de calidad agrupadas por sección temática")
public class CalidadPorSeccionDTO {
    
    @Schema(example = "BD", description = "Nombre de la sección temática")
    private String seccion;
    
    @Schema(example = "45", description = "Número de valoraciones positivas en esta sección")
    private long positivas;
    
    @Schema(example = "5", description = "Número de valoraciones negativas en esta sección")
    private long negativas;
    
    @Schema(example = "0.9", description = "Ratio de calidad de la sección")
    private double ratio;
    
    @Schema(example = "50", description = "Total de preguntas realizadas en esta sección")
    private long totalPreguntas;
}