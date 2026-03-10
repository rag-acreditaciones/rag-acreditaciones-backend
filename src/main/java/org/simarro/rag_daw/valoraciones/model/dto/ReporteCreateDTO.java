package org.simarro.rag_daw.valoraciones.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReporteCreateDTO {

    @NotNull(message = "El mensajeId es obligatorio")
    private Long mensajeId;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String descripcion;
}