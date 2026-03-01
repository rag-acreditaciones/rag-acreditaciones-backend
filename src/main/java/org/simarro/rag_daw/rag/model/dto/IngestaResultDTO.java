package org.simarro.rag_daw.rag.model.dto;
import java.util.List;

import jakarta.validation.constraints.*;

public record IngestaResultDTO(
    @PositiveOrZero(message = "El total de chunks no puede ser negativo")
    int totalChunks,
    
    @NotEmpty(message = "La lista de IDs del Vector Store no puede estar vacía")
    List<String> vectorStoreIds,
    
    @PositiveOrZero(message = "El conteo de tokens debe ser cero o superior")
    int totalTokens,
    
    @NotNull(message = "Debe referenciar a una configuración RAG")
    Long ragConfiguracionId,
    
    @NotBlank(message = "El nombre del RAG es obligatorio para el reporte")
    String ragNombre,
    
    @NotBlank(message = "Debe indicarse el modelo de embedding utilizado")
    String modeloEmbeddingUsado,
    
    @Positive(message = "Las dimensiones deben ser un número positivo")
    Integer dimensionesUsadas
) {}