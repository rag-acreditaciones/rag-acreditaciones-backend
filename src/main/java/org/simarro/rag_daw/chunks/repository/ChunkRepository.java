package org.simarro.rag_daw.chunks.repository;

import java.util.Optional;
import java.util.UUID;

import org.simarro.rag_daw.chunks.model.DB.DocumentoChunkDb;
import org.simarro.rag_daw.chunks.model.ENUMS.EstadoChunk;
import org.simarro.rag_daw.chunks.model.interfaces.DocumentoChunkStatsInterface;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import lombok.NonNull;

@Repository
public interface ChunkRepository
      extends JpaRepository<DocumentoChunkDb, Long>, JpaSpecificationExecutor<DocumentoChunkDb> {

   Optional<DocumentoChunkDb> findByVectorStoreId(@NonNull UUID uuid);

   Page<DocumentoChunkDb> findByDocumentosId(@NonNull Long docId, Pageable pageable);

   Page<DocumentoChunkDb> findByDocumentosIdAndEstado(@NonNull Long docId, EstadoChunk estado, Pageable pageable);

   @Query("SELECT c FROM DocumentoChunkDb c WHERE c.textoCompleto ILIKE %:texto%")
   Page<DocumentoChunkDb> buscarPorTexto(@Param("texto") String texto, @NonNull Pageable pageable);

   @Query("SELECT c FROM DocumentoChunkDb c WHERE c.textoCompleto ILIKE %:texto% AND c.documentos.seccionTematica.id = :seccionId")
   Page<DocumentoChunkDb> buscarPorTextoYSeccion(@Param("texto") String texto, @Param("seccionId") Long seccionId,
         @NonNull Pageable pageable);

   @Query(nativeQuery = true, value = "SELECT documento_id, COUNT(*) AS numero_chunks, SUM(num_tokens) AS total_tokens, COUNT(CASE WHEN estado_chunk = 'PENDIENTE' THEN 1 END) AS estado_pendiente, COUNT(CASE WHEN estado_chunk = 'REVISADO' THEN 1 END) AS estado_revisado, COUNT(CASE WHEN estado_chunk = 'DESCARTADO' THEN 1 END) AS estado_descartado, MAX(LENGTH(texto_chunk)) AS longitud_max, MIN(LENGTH(texto_chunk)) AS longitud_min, AVG(LENGTH(texto_chunk)) AS longitud_media FROM chunks WHERE documento_id = :docId GROUP BY documento_id")
   DocumentoChunkStatsInterface chunkStats(@NonNull Long docId);
}
