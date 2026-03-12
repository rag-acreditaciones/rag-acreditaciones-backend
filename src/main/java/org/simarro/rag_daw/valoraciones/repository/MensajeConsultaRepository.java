package org.simarro.rag_daw.valoraciones.repository;

import java.util.List;

import org.simarro.rag_daw.model.db.MensajeDb;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeConsultaRepository extends JpaRepository<MensajeDb, Long> {

    List<MensajeDb> findByConversacionId(Long conversacionId);
}