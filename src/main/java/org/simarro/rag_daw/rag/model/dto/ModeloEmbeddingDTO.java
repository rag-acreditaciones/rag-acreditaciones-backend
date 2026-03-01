package org.simarro.rag_daw.rag.model.dto;
import jakarta.validation.constraints.*;

import org.simarro.rag_daw.rag.model.enums.Proveedor;

public record ModeloEmbeddingDTO(
    Long id, // El ID suele ser nulo al crear, pero @Positive si viene de la BD
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    String nombre,
    
    @NotNull(message = "El proveedor no puede ser nulo")
    Proveedor proveedor,
    
    @NotBlank(message = "El modeloId de la API es necesario")
    String modeloId,
    
    @Positive(message = "Las dimensiones deben ser un número positivo")
    Integer dimensiones,
    
    String descripcion,
    Boolean activo
) {}
