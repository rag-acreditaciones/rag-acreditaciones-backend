package org.simarro.rag_daw.rag.model.db;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rag_configuraciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RagConfiguracionDb {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
    private String descripcion;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modelo_embedding_id", nullable = false)
    private ModeloEmbeddingDb modeloEmbedding;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "modelo_llm_id", nullable = false)
    private ModeloLlmDb modeloLlm;
    @Column(name = "host_embedding", nullable = false)
    private String hostEmbedding;
    @Column(name = "puerto_embedding", nullable = false)
    private Integer puertoEmbedding;
    @Column(name = "host_llm", nullable = false)
    private String hostLlm;
    @Column(name = "puerto_llm", nullable = false)
    private Integer puertoLlm;
    @Column(name = "esquema_vector_store", nullable = false, length = 100)
    private String esquemaVectorStore;
    @Column(nullable = false)
    private Boolean activo = true;
    @Column(name = "por_defecto", nullable = false)
    private Boolean porDefecto = false;
    @Column(name = "requiere_gpu", nullable = false)
    private Boolean requiereGpu = false;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}