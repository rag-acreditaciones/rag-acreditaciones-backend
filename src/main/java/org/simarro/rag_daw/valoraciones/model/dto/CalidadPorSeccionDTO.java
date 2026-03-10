package org.simarro.rag_daw.valoraciones.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalidadPorSeccionDTO {

    private String seccion;
    private long positivas;
    private long negativas;
    private double ratio;
    private long totalPreguntas;
}