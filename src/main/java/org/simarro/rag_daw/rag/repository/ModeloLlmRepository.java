package org.simarro.rag_daw.rag.repository;

import java.util.List;
import java.util.Optional;

import org.simarro.rag_daw.rag.model.db.ModeloLlmDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloLlmRepository
        extends JpaRepository<ModeloLlmDb, Long>,
                JpaSpecificationExecutor<ModeloLlmDb> {
    List<ModeloLlmDb> findByActivoTrue();
    Optional<ModeloLlmDb> findByNombre(String nombre);
}
