package org.simarro.rag_daw.valoraciones.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionCreateDTO {
    
    @NotNull(message = "El ID del mensaje es obligatorio")
    private Long mensajeId;
    
    @NotNull(message = "La valoración es obligatoria")
    @Size(min = 8, max = 8, message = "La valoración debe ser POSITIVA o NEGATIVA")
    private String valoracion;
    
    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres")
    private String comentario;
}