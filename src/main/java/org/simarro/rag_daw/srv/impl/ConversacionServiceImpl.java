package org.simarro.rag_daw.srv.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.simarro.rag_daw.model.db.ConversacionDb;
import org.simarro.rag_daw.model.db.MensajeDb;
import org.simarro.rag_daw.model.dto.ConversacionCreateDTO;
import org.simarro.rag_daw.model.dto.ConversacionDetailDTO;
import org.simarro.rag_daw.model.dto.ConversacionResponseDTO;
import org.simarro.rag_daw.model.enums.EstadoConversacion;
import org.simarro.rag_daw.rag.model.dto.ChatResponseDTO;
import org.simarro.rag_daw.rag.srv.ChatRagService;
import org.simarro.rag_daw.repository.ConversacionRepository;
import org.simarro.rag_daw.repository.MensajeRepository;
import org.simarro.rag_daw.srv.ConversacionService;

@Service
@RequiredArgsConstructor
public class ConversacionServiceImpl implements ConversacionService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final ChatRagService chatRagService;

    @Override
    public ConversacionDetailDTO crearConversacion(@NonNull ConversacionCreateDTO dto) {

        ConversacionDb conversacion = new ConversacionDb();

        conversacion.setUsuarioId(dto.getUsuarioId());
        conversacion.setTitulo(dto.getTitulo());
        conversacion.setSeccionTematica(dto.getSeccionTematica());
        conversacion.setEstado(dto.getEstado());
        conversacion.setFechaCreacion(LocalDateTime.now());

        ConversacionDb saved = conversacionRepository.save(conversacion);

        return new ConversacionDetailDTO(
                saved.getId(),
                saved.getUsuarioId(),
                saved.getTitulo(),
                saved.getSeccionTematica(),
                saved.getEstado(),
                saved.getFechaCreacion(),
                List.of()
        );
    }

    @Override
    public Page<ConversacionResponseDTO> listarConversaciones(
            @NonNull Long usuarioId,
            String seccionTematica,
            EstadoConversacion estado,
            Pageable pageable) {

        Page<ConversacionDb> page;

        if (seccionTematica != null && estado != null) {
            page = conversacionRepository.findByUsuarioIdAndSeccionTematicaAndEstado(usuarioId, seccionTematica, estado, pageable);
        } else if (seccionTematica != null) {
            page = conversacionRepository.findByUsuarioIdAndSeccionTematica(usuarioId, seccionTematica, pageable);
        } else if (estado != null) {
            page = conversacionRepository.findByUsuarioIdAndEstado(usuarioId, estado, pageable);
        } else {
            page = conversacionRepository.findByUsuarioId(usuarioId, pageable);
        }

        return page.map(conversacion -> {

            List<MensajeDb> mensajes =
                    mensajeRepository.findByConversacionIdOrderByFechaAsc(conversacion.getId());

            MensajeDb ultimoMensaje =
                    mensajes.isEmpty() ? null : mensajes.get(mensajes.size() - 1);

            return new ConversacionResponseDTO(
                    conversacion.getId(),
                    conversacion.getUsuarioId(),
                    conversacion.getTitulo(),
                    conversacion.getSeccionTematica(),
                    conversacion.getEstado(),
                    conversacion.getFechaCreacion(),
                    ultimoMensaje
            );
        });
    }

    @Override
    public Optional<ConversacionDetailDTO> obtenerConversacion(@NonNull Long conversacionId) {
        Optional<ConversacionDb> conversacion = conversacionRepository.findById(conversacionId);
        if (conversacion.isEmpty()) return Optional.empty();
        List<MensajeDb> mensajes = mensajeRepository.findByConversacionIdOrderByFechaAsc(conversacionId);
        ConversacionDb c = conversacion.get();

        return Optional.of(new ConversacionDetailDTO(
                c.getId(),
                c.getUsuarioId(),
                c.getTitulo(),
                c.getSeccionTematica(),
                c.getEstado(),
                c.getFechaCreacion(),
                mensajes
        ));
    }

    @Override
    public Optional<ConversacionDetailDTO> archivarConversacion(@NonNull Long conversacionId, @NonNull Long usuarioId) {
        Optional<ConversacionDb> conversacion = conversacionRepository.findById(conversacionId);
        if (conversacion.isPresent() && conversacion.get().getUsuarioId().equals(usuarioId)) {
            ConversacionDb c = conversacion.get();
            c.setEstado(EstadoConversacion.ARCHIVADA);
            conversacionRepository.save(c);
            return obtenerConversacion(conversacionId);
        }
        return Optional.empty();
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public boolean eliminarConversacion(@NonNull Long conversacionId,@NonNull Long usuarioId) {
        Optional<ConversacionDb> conversacion = conversacionRepository.findById(conversacionId);

        if (conversacion.isPresent() && conversacion.get().getUsuarioId().equals(usuarioId)) {
            mensajeRepository.deleteByConversacionId(conversacionId);
            conversacionRepository.delete(conversacion.get());
            return true;
        }
        return false;
    }

    @Override
    public String generarTitulo(@NonNull String primerMensaje) {
        String prompt = """
                Genera un título corto y descriptivo (máximo 6 palabras) para una conversación 
                que empieza con este mensaje: "%s".
                Responde SOLO con el título, sin comillas ni explicaciones.
                """.formatted(primerMensaje);

        ChatResponseDTO response = chatRagService.preguntar(prompt, null);
        String titulo = response.respuesta().trim();

        // Seguridad: si la IA devuelve algo muy largo, recortamos
        return titulo.length() > 60 ? titulo.substring(0, 60) + "…" : titulo;
}
}