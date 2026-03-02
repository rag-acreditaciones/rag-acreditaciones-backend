package org.simarro.rag_daw.repository;

import java.util.List;

import org.simarro.rag_daw.model.db.MensajeDb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<MensajeDb, Long> {

    // Obtener todos los mensajes de una conversación ordenados por fecha
    List<MensajeDb> findByConversacionIdOrderByFechaAsc(Long conversacionId);

    // Borrar todos los mensajes de una conversación (si se elimina la conversación)
    void deleteByConversacionId(Long conversacionId);
}