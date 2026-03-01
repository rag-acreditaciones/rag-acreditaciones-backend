package org.simarro.rag_daw.rag.model.db;

import java.time.LocalDateTime;

import org.simarro.rag_daw.rag.model.enums.Proveedor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "modelos_embedding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModeloEmbeddingDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Proveedor proveedor;
    @Column(name = "modelo_id", nullable = false, length = 200)
    private String modeloId;
    @Column(nullable = false)
    private Integer dimensiones;
    private String descripcion;
    @Column(nullable = false)
    private Boolean activo = true;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
