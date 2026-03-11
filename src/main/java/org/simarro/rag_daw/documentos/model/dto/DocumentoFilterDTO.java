package org.simarro.rag_daw.documentos.model.dto;

import java.time.LocalDateTime;

public record DocumentoFilterDTO(
        String nombre,
        Long seccionId,
        String subidoPor,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta,
        String estado) {
}
