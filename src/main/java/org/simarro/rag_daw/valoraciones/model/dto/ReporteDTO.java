package org.simarro.rag_daw.valoraciones.model.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReporteDTO {

    private Long id;

    private Long mensajeId;

    private Long usuarioId;

    private String usuarioEmail;

    private String motivo;

    private String descripcion;

    private String estado;

    private LocalDateTime fechaCreacion;
}