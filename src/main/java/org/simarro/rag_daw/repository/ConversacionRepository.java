package org.simarro.rag_daw.repository;

import java.util.List;

import org.simarro.rag_daw.model.db.ConversacionDb;
import org.simarro.rag_daw.model.enums.EstadoConversacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversacionRepository extends JpaRepository<ConversacionDb, Long> {

    // Listar todas las conversaciones de un usuario
    List<ConversacionDb> findByUsuarioId(Long usuarioId);

    // Listar conversaciones de un usuario por estado (ACTIVA / ARCHIVADA)
    List<ConversacionDb> findByUsuarioIdAndEstado(Long usuarioId, EstadoConversacion estado);
}