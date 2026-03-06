package org.simarro.rag_daw.valoraciones.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class CalidadResumenDTO {
    private long totalValoraciones;
    private long positivas;
    private long negativas;
    private double ratioCalidad;
    private long totalReportes;
    private long reportesPendientes;
}