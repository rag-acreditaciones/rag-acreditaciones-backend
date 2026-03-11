package org.simarro.rag_daw.valoraciones.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "DTO para cambiar el estado de un reporte")
public class ReporteEstadoDTO {
    
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PENDIENTE|REVISADO|DESCARTADO", 
             message = "El estado debe ser: PENDIENTE, REVISADO o DESCARTADO")
    @Schema(example = "REVISADO", description = "Nuevo estado del reporte", 
            allowableValues = {"PENDIENTE", "REVISADO", "DESCARTADO"})
    private String estado;
}