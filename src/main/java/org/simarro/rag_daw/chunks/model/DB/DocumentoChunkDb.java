package org.simarro.rag_daw.chunks.model.DB;

import java.util.UUID;

import org.simarro.rag_daw.chunks.model.ENUMS.EstadoChunk;
import org.simarro.rag_daw.documentos.model.db.DocumentoDb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "chunks")
public class DocumentoChunkDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "orden_chunk", nullable = false)
    private Integer orden;
    @Column(name = "texto_chunk", columnDefinition = "TEXT", nullable = false)
    private String textoCompleto;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_chunk", nullable = false)
    private EstadoChunk estado = EstadoChunk.PENDIENTE;
    @Column(name = "num_tokens", nullable = false)
    private Integer numTokens;
    @Column(name = "vector_store_id", nullable = false)
    private UUID vectorStoreId;
    @ManyToOne
    @JoinColumn(name = "documento_id", nullable = false)
    private DocumentoDb documentos;

}

/*
CREATE TABLE IF NOT EXISTS chunks (
    id        UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    orden_chunk   TEXT NOT NULL,
    texto_chunk   TEXT NOT NULL,
    estado_chunk   VARCHAR(20) NOT NULL  DEFAULT 'PENDIENTE',
  CONSTRAINT usuarios_estado_check CHECK (estado IN (
    'PENDIENTE','REVISADO','DESCARTADO')),
    num_tokens  INTEGER NOT NULL,
    vector_id  UUID NOT NULL,
    documento_id UUID NOT NULL REFERENCES documentos(id) ON DELETE CASCADE
);
*/
