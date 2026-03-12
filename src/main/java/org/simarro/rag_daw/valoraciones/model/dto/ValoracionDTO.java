package org.simarro.rag_daw.valoraciones.model.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Respuesta con los datos completos de una valoración")
public class ValoracionDTO {
    
    @Schema(example = "1", description = "ID único de la valoración")
    private Long id;
    
    @Schema(example = "2", description = "ID del mensaje valorado")
    private Long mensajeId;
    
    @Schema(example = "5", description = "ID del usuario que realizó la valoración")
    private Long usuarioId;
    
    @Schema(example = "POSITIVA", description = "Tipo de valoración (POSITIVA/NEGATIVA)")
    private String valoracion;
    
    @Schema(example = "Muy útil, responde exactamente lo que necesitaba", description = "Comentario de la valoración")
    private String comentario;
    
    @Schema(example = "2026-03-11T10:30:00", description = "Fecha y hora de creación")
    private LocalDateTime fechaCreacion;
}