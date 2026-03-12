package org.simarro.rag_daw.valoraciones.model.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Resumen de valoraciones de una conversación")
public class ValoracionResumenDTO {
    
    @Schema(example = "15", description = "Número total de valoraciones positivas")
    private long totalPositivas;
    
    @Schema(example = "3", description = "Número total de valoraciones negativas")
    private long totalNegativas;
    
    @Schema(example = "0.833", description = "Ratio de calidad (positivas / total)")
    private double ratio;
    
    @Schema(description = "Lista detallada de valoraciones")
    private List<ValoracionDTO> detalles;
}