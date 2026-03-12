package org.simarro.rag_daw.reportes.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenDTO {
    private Long totalDocumentos;
    private Long totalChunks;
    private Long totalConversaciones;
    private Long totalPreguntas;
    private Long totalUsuarios;
    private Double ratioCalidad;
}