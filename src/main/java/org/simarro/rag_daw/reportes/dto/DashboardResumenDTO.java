package org.simarro.rag_daw.reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DashboardResumenDTO {
    private Long totalDocumentos;
    private Long totalChunks;
    private Long totalConversaciones;
    private Long totalPreguntas;
    private Double ratioCalidad;   // % valoraciones positivas
}
