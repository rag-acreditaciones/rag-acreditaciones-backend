package org.simarro.rag_daw.valoraciones.model.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalidadEvolucionDTO {

    private LocalDate fecha;
    private long positivas;
    private long negativas;
    private double ratio;
    private long totalReportes;
}