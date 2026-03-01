package org.simarro.rag_daw.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import org.simarro.rag_daw.model.db.RolDb;
import org.simarro.rag_daw.model.enums.RolNombre;

public interface RolRepository extends JpaRepository<RolDb, Integer> {
    Optional<RolDb> findByNombre(RolNombre rolNombre);
}

