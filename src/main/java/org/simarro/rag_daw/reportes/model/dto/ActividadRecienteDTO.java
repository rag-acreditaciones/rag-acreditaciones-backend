package org.simarro.rag_daw.reportes.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActividadRecienteDTO {
    private String usuario;
    private String accion;
    private String recurso;
    private String fecha;
}