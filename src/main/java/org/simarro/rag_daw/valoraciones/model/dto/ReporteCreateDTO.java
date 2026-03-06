package org.simarro.rag_daw.valoraciones.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class ReporteCreateDTO {
    @NotNull private Long mensajeId;
    @NotNull private String motivo;
    private String descripcion;
}