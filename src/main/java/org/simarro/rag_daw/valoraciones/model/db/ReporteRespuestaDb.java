package org.simarro.rag_daw.valoraciones.model.db;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "reportes_respuesta")
public class ReporteRespuestaDb {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El ID del mensaje es obligatorio")
    @Column(name = "mensaje_id", nullable = false)
    private Long mensajeId;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @NotNull(message = "El motivo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotivoReporte motivo;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String descripcion;
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReporte estado = EstadoReporte.PENDIENTE;
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    public enum MotivoReporte {
        INCORRECTA("Incorrecta", "La respuesta contiene información errónea"),
        INCOMPLETA("Incompleta", "La respuesta no cubre todos los aspectos de la pregunta"),
        IRRELEVANTE("Irrelevante", "La respuesta no está relacionada con la pregunta"),
        OFENSIVA("Ofensiva", "La respuesta contiene lenguaje o contenido ofensivo"),
        OTRA("Otra", "Otro motivo no contemplado");
        
        private final String nombre;
        private final String descripcion;
        
        MotivoReporte(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }
        
        public String getNombre() {
            return nombre;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum EstadoReporte {
        PENDIENTE("Pendiente", "Reporte pendiente de revisión"),
        REVISADO("Revisado", "Reporte revisado y procesado"),
        DESCARTADO("Descartado", "Reporte descartado por no ser válido");
        
        private final String nombre;
        private final String descripcion;
        
        EstadoReporte(String nombre, String descripcion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
        }
        
        public String getNombre() {
            return nombre;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
}