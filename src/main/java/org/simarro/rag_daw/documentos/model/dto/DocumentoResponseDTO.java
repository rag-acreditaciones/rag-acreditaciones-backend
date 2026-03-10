package org.simarro.rag_daw.documentos.model.dto;

import java.time.LocalDateTime;

public record DocumentoResponseDTO(
        Long id,
        String nombreFichero,
        String contentType,
        Long sizeBytes,
        String descripcion,
        Long seccionTematicaId,
        String seccionTematicaNombre,
        String subidoPor,
        String estado,
        LocalDateTime fechaSubida) {
}
