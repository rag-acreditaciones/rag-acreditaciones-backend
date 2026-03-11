package org.simarro.rag_daw.valoraciones.model.db;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "valoraciones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"mensaje_id", "usuario_id"})
})
public class ValoracionDb {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "mensaje_id", nullable = false)
    private Long mensajeId;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoValoracion valoracion;
    
    private String comentario;
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false)  // AÑADE ESTO
    private LocalDateTime fechaCreacion;
    
    public enum TipoValoracion { POSITIVA, NEGATIVA }
}