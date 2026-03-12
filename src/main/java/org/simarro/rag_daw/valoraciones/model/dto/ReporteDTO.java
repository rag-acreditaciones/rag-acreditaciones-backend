package org.simarro.rag_daw.valoraciones.model.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Respuesta con los datos completos de un reporte")
public class ReporteDTO {
    
    @Schema(example = "6", description = "ID único del reporte")
    private Long id;
    
    @Schema(example = "4", description = "ID del mensaje reportado")
    private Long mensajeId;
    
    @Schema(example = "5", description = "ID del usuario que creó el reporte")
    private Long usuarioId;
    
    @Schema(example = "candidato@example.com", description = "Email del usuario que creó el reporte")
    private String usuarioEmail;
    
    @Schema(example = "INCORRECTA", description = "Motivo del reporte")
    private String motivo;
    
    @Schema(example = "La información sobre plazos está desactualizada", description = "Descripción del reporte")
    private String descripcion;
    
    @Schema(example = "PENDIENTE", description = "Estado actual del reporte", allowableValues = {"PENDIENTE", "REVISADO", "DESCARTADO"})
    private String estado;
    
    @Schema(example = "2026-03-11T10:35:00", description = "Fecha y hora de creación")
    private LocalDateTime fechaCreacion;
}