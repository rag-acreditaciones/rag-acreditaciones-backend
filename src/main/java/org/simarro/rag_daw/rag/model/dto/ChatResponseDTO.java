package org.simarro.rag_daw.rag.model.dto;
import java.util.List;

import jakarta.validation.constraints.*;

public record ChatResponseDTO(
    @NotBlank(message = "La respuesta no puede estar vacía")
    String respuesta,
    
    @NotEmpty(message = "Debe haber al menos un chunk de referencia")
    List<String> chunksUtilizadosIds,
    
    @PositiveOrZero
    long tiempoRespuestaMs,
    
    @NotNull
    Long ragConfiguracionId,
    
    @NotBlank
    String ragNombre,
    
    @NotBlank
    String modeloLlmUsado
) {}