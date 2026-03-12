package org.simarro.rag_daw.repository;

import java.util.List;

import org.simarro.rag_daw.model.db.ConversacionDb;
import org.simarro.rag_daw.model.enums.EstadoConversacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ConversacionRepository  extends JpaRepository<ConversacionDb, Long>,JpaSpecificationExecutor<ConversacionDb>{

    // Listar todas las conversaciones de un usuario
    List<ConversacionDb> findByUsuarioId(Long usuarioId);

    // Listar conversaciones de un usuario por estado (ACTIVA / ARCHIVADA)
    List<ConversacionDb> findByUsuarioIdAndEstado(Long usuarioId, EstadoConversacion estado);

    // Paginación sin filtros
    Page<ConversacionDb> findByUsuarioId(Long usuarioId, Pageable pageable);

    // Paginación con filtro por estado
    Page<ConversacionDb> findByUsuarioIdAndEstado(Long usuarioId, EstadoConversacion estado, Pageable pageable);

    // Paginación con filtro por sección temática
    Page<ConversacionDb> findByUsuarioIdAndSeccionTematica(Long usuarioId, String seccionTematica, Pageable pageable);

    // Paginación con filtro por sección temática y estado
    Page<ConversacionDb> findByUsuarioIdAndSeccionTematicaAndEstado(
            Long usuarioId,
            String seccionTematica,
            EstadoConversacion estado,
            Pageable pageable
    );
}

