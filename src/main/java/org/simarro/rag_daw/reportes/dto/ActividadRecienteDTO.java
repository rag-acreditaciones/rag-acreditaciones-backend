package org.simarro.rag_daw.reportes.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ActividadRecienteDTO {
    private String usuario;
    private String accion;      // "SUBIO_DOCUMENTO", "HIZO_PREGUNTA"...
    private LocalDateTime fecha;
}

