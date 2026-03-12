package org.simarro.rag_daw.valoraciones.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "DTO para crear un reporte de respuesta")
public class ReporteCreateDTO {
    
    @NotNull(message = "El mensajeId es obligatorio")
    @Schema(example = "4", description = "ID del mensaje que se reporta")
    private Long mensajeId;
    
    @NotBlank(message = "El motivo es obligatorio")
    @Pattern(regexp = "INCORRECTA|INCOMPLETA|IRRELEVANTE|OFENSIVA|OTRA", 
             message = "El motivo debe ser: INCORRECTA, INCOMPLETA, IRRELEVANTE, OFENSIVA u OTRA")
    @Schema(example = "INCORRECTA", description = "Motivo del reporte", 
            allowableValues = {"INCORRECTA", "INCOMPLETA", "IRRELEVANTE", "OFENSIVA", "OTRA"})
    private String motivo;
    
    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    @Schema(example = "La información sobre plazos está desactualizada", description = "Descripción detallada del problema")
    private String descripcion;
}