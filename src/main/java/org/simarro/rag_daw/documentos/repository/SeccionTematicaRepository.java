package org.simarro.rag_daw.documentos.repository;

import org.simarro.rag_daw.documentos.model.db.SeccionTematicaDb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeccionTematicaRepository extends JpaRepository<SeccionTematicaDb, Long> {
}
