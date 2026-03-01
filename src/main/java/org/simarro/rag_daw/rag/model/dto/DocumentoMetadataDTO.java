package org.simarro.rag_daw.rag.model.dto;

import jakarta.validation.constraints.*;

public record DocumentoMetadataDTO(
    @NotNull(message = "El ID del documento es obligatorio")
    Long documentoId,
    
    @NotBlank(message = "El nombre del fichero no puede estar vacío")
    String nombreFichero,
    
    // La sección temática podría ser opcional, por eso no ponemos @NotBlank si no es estrictamente necesario
    String seccionTematica, 
    
    @NotBlank(message = "Es necesario saber quién subió el documento")
    String subidoPor
) {}