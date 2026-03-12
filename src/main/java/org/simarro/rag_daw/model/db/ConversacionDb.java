package org.simarro.rag_daw.model.db;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.simarro.rag_daw.model.enums.EstadoConversacion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversaciones")
public class ConversacionDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK lógica al usuario (sin relación @ManyToOne)
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String titulo = "Nueva conversación";

    @Column(name = "seccion_tematica")
    private String seccionTematica;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoConversacion estado = EstadoConversacion.ACTIVA;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}