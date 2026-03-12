package org.simarro.rag_daw.valoraciones.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Información de respuesta destacada en ranking")
public class TopRespuestaDTO {
    
    @Schema(example = "2", description = "ID del mensaje")
    private Long mensajeId;
    
    @Schema(example = "¿Qué es el Catálogo Nacional...", description = "Texto resumido de la respuesta (primeros caracteres)")
    private String textoResumido;
    
    @Schema(example = "25", description = "Número de valoraciones positivas")
    private Long valoracionesPositivas;
    
    @Schema(example = "3", description = "Número de valoraciones negativas")
    private Long valoracionesNegativas;
    
    @Schema(example = "1", description = "ID de la conversación a la que pertenece")
    private Long conversacionId;
}