package org.simarro.rag_daw.valoraciones.model.db;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reportes_respuesta")
public class ReporteRespuestaDb {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "mensaje_id", nullable = false)
    private Long mensajeId;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotivoReporte motivo;
    
    private String descripcion;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReporte estado = EstadoReporte.PENDIENTE;
    
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    public enum MotivoReporte { INCORRECTA, INCOMPLETA, IRRELEVANTE, OFENSIVA, OTRA }
    public enum EstadoReporte { PENDIENTE, REVISADO, DESCARTADO }
}