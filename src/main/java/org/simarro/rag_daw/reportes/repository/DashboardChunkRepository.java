package org.simarro.rag_daw.reportes.repository;

import org.simarro.rag_daw.chunks.model.db.DocumentoChunkDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DashboardChunkRepository extends JpaRepository<DocumentoChunkDb, Long> {

    @Query("SELECT COUNT(c) FROM DocumentoChunkDb c")
    Long countTotal();
}