package org.simarro.rag_daw.srv;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import org.simarro.rag_daw.model.dto.ConversacionCreateDTO;
import org.simarro.rag_daw.model.dto.ConversacionDetailDTO;
import org.simarro.rag_daw.model.dto.ConversacionResponseDTO;
import org.simarro.rag_daw.model.enums.EstadoConversacion;

public interface ConversacionService {

    ConversacionDetailDTO crearConversacion(@NonNull ConversacionCreateDTO dto);

    Page<ConversacionResponseDTO> listarConversaciones(@NonNull Long usuarioId, String seccionTematica, EstadoConversacion estado,Pageable pageable);

    Optional<ConversacionDetailDTO> obtenerConversacion(@NonNull Long conversacionId);

    Optional<ConversacionDetailDTO> archivarConversacion(@NonNull Long conversacionId,@NonNull Long usuarioId);

    boolean eliminarConversacion(@NonNull Long conversacionId, @NonNull Long usuarioId);

    String generarTitulo(@NonNull String primerMensaje);
}