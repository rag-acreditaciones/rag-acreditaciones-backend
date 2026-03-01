package org.simarro.rag_daw.rag.repository;

import java.util.List;
import java.util.Optional;

import org.simarro.rag_daw.rag.model.db.ModeloEmbeddingDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloEmbeddingRepository
        extends JpaRepository<ModeloEmbeddingDb, Long>,
                JpaSpecificationExecutor<ModeloEmbeddingDb> {
    List<ModeloEmbeddingDb> findByActivoTrue();
    Optional<ModeloEmbeddingDb> findByNombre(String nombre);
}