package org.simarro.rag_daw.repository;

import java.util.List;
import java.util.Optional;

import org.simarro.rag_daw.model.db.MensajeDb;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepository extends JpaRepository<MensajeDb, Long> {

    // Listar todos los mensajes de una conversación, ordenados por fecha ascendente
    List<MensajeDb> findByConversacionIdOrderByFechaAsc(Long conversacionId);

    // Contar mensajes de una conversación (para saber si es el primer mensaje)
    long countByConversacionId(Long conversacionId);

    // Obtener el último mensaje de una conversación (para mostrar en listados)
    Optional<MensajeDb> findTopByConversacionIdOrderByFechaDesc(Long conversacionId);

    // Borrar todos los mensajes de una conversación (cuando eliminas la
    // conversación)
    void deleteByConversacionId(Long conversacionId);

    Page<MensajeDb> findByConversacionIdOrderByFechaAsc(Long conversacionId, Pageable pageable);

}