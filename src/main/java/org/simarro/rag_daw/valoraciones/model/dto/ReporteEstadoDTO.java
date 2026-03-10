package org.simarro.rag_daw.valoraciones.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReporteEstadoDTO {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}