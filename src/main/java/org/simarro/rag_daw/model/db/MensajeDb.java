package org.simarro.rag_daw.model.db;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.simarro.rag_daw.model.enums.TipoMensaje;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mensajes")
public class MensajeDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK lógica a conversación
    @Column(name = "conversacion_id", nullable = false)
    private Long conversacionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMensaje tipo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    /**
     * IDs de chunks usados por el RAG.
     * Se guarda como JSON array (ej: [12, 45, 78])
     */

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chunks_utilizados", columnDefinition = "JSON")
    private List<Long> chunksUtilizados;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
}