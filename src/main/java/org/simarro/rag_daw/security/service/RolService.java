package org.simarro.rag_daw.security.service;

import java.util.Optional;

import org.springframework.lang.NonNull;

import org.simarro.rag_daw.model.db.RolDb;
import org.simarro.rag_daw.model.enums.RolNombre;

public interface RolService {
    public Optional<RolDb> getByRolNombre(RolNombre rolNombre);

    public void save(@NonNull RolDb rol);
}
