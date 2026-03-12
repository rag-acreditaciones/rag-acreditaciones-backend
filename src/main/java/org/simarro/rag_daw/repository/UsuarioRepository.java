package org.simarro.rag_daw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.simarro.rag_daw.model.db.UsuarioDb;

public interface UsuarioRepository extends JpaRepository<UsuarioDb, Long> {
    boolean existsByEmail(String email);

    Optional<UsuarioDb> findByEmail(String email);
}
