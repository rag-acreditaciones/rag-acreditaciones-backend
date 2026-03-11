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
@Table(name = "valoraciones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"mensaje_id", "usuario_id"})
})
public class ValoracionDb {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El ID del mensaje es obligatorio")
    @Column(name = "mensaje_id", nullable = false)
    private Long mensajeId;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @NotNull(message = "La valoración es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoValoracion valoracion;
    
    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    @Column(length = 500)
    private String comentario;
    
    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    public enum TipoValoracion {
        POSITIVA("Positiva", "El usuario considera útil la respuesta"),
        NEGATIVA("Negativa", "El usuario considera que la respuesta no es útil");
        
        private final String nombre;
        private final String descripcion;
        
        TipoValoracion(String nombre, String descripcion) {
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