package org.simarro.rag_daw.rag.model.dto;
import jakarta.validation.constraints.*;

public record RagConfiguracionDTO(
    Long id,
    @NotBlank @Size(min = 3, max = 50)
    String nombre,
    
    @NotNull
    ModeloEmbeddingDTO modeloEmbedding, // Valida que el objeto no sea nulo
    
    @NotBlank
    String hostLlm,
    
    @Min(1) @Max(65535)
    Integer puertoLlm,
    
    @NotBlank
    String esquemaVectorStore,
    
    @NotNull
    Boolean activo
) {}