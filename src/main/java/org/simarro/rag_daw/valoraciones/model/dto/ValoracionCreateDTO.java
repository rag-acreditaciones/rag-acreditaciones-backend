package org.simarro.rag_daw.valoraciones.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "DTO para crear o actualizar una valoración")
public class ValoracionCreateDTO {
    
    @NotNull(message = "El ID del mensaje es obligatorio")
    @Schema(example = "2", description = "ID del mensaje a valorar")
    private Long mensajeId;
    
    @NotNull(message = "La valoración es obligatoria")
    @Pattern(regexp = "POSITIVA|NEGATIVA", message = "La valoración debe ser POSITIVA o NEGATIVA")
    @Schema(example = "POSITIVA", description = "Tipo de valoración (POSITIVA/NEGATIVA)", allowableValues = {"POSITIVA", "NEGATIVA"})
    private String valoracion;
    
    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres")
    @Schema(example = "Muy útil, responde exactamente lo que necesitaba", description = "Comentario opcional sobre la valoración")
    private String comentario;
}