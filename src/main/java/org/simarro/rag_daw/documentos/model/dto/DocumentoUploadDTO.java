package org.simarro.rag_daw.documentos.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DocumentoUploadDTO(

        @NotNull(message = "La seccion tematica es obligatoria") Long seccionTematicaId,

        @Size(max = 500, message = "La descripcion no puede superar 500 caracteres") String descripcion) {
}
