package org.simarro.rag_daw.srv.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.simarro.rag_daw.model.db.ConversacionDb;
import org.simarro.rag_daw.model.db.MensajeDb;
import org.simarro.rag_daw.model.dto.MensajeDTO;
import org.simarro.rag_daw.model.dto.PreguntaDTO;
import org.simarro.rag_daw.model.enums.EstadoConversacion;
import org.simarro.rag_daw.model.enums.TipoMensaje;
import org.simarro.rag_daw.rag.model.dto.ChatResponseDTO;
import org.simarro.rag_daw.rag.srv.ChatRagService;
import org.simarro.rag_daw.repository.ConversacionRepository;
import org.simarro.rag_daw.repository.MensajeRepository;
import org.simarro.rag_daw.srv.ConversacionService;
import org.simarro.rag_daw.srv.MensajeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MensajeServiceImpl implements MensajeService {

    private final MensajeRepository mensajeRepository;
    private final ConversacionRepository conversacionRepository;
    private final ChatRagService chatRagService;
    private final ConversacionService conversacionService;

    @Override
    public MensajeDTO enviarPregunta(PreguntaDTO preguntaDTO) {
        // 1. Resolver o crear la conversación
        Long conversacionId = resolverConversacion(preguntaDTO);

        // ✅ 2. Si es el primer mensaje, generar título con IA y actualizarlo
        if (mensajeRepository.countByConversacionId(conversacionId) == 0) {
            String titulo = conversacionService.generarTitulo(preguntaDTO.getPregunta());
            ConversacionDb conversacion = conversacionRepository.findById(conversacionId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Conversación no encontrada: " + conversacionId));
            conversacion.setTitulo(titulo);
            conversacionRepository.save(conversacion);
        }

        // 3. Persistir la PREGUNTA
        MensajeDb pregunta = new MensajeDb();
        pregunta.setConversacionId(conversacionId);
        pregunta.setTipo(TipoMensaje.PREGUNTA);
        pregunta.setContenido(preguntaDTO.getPregunta());
        pregunta.setFecha(LocalDateTime.now());
        mensajeRepository.save(pregunta);

        // 4. Invocar el ChatRagService
        ChatResponseDTO chatResponse = chatRagService.preguntar(
                preguntaDTO.getPregunta(),
                preguntaDTO.getSeccionTematica());

        // 5. Convertir chunk IDs de String a Long
        List<Long> chunkIds = chatResponse.chunksUtilizadosIds().stream()
                .map(id -> {
                    try {
                        return Long.parseLong(id);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 6. Persistir la RESPUESTA
        MensajeDb respuesta = new MensajeDb();
        respuesta.setConversacionId(conversacionId);
        respuesta.setTipo(TipoMensaje.RESPUESTA);
        respuesta.setContenido(chatResponse.respuesta());
        respuesta.setChunksUtilizados(chunkIds);
        respuesta.setFecha(LocalDateTime.now());
        MensajeDb respuestaGuardada = mensajeRepository.save(respuesta);

        // 7. Devolver la respuesta como DTO
        return toDTO(respuestaGuardada);
    }

    @Override
    public Page<MensajeDTO> findByConversacionId(Long conversacionId, Pageable pageable) {
        return mensajeRepository
                .findByConversacionIdOrderByFechaAsc(conversacionId, pageable)
                .map(this::toDTO);
    }

    // --- PRIVADOS ---

    private Long resolverConversacion(PreguntaDTO dto) {
        if (dto.getConversacionId() != null) {
            conversacionRepository.findById(dto.getConversacionId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Conversación no encontrada: " + dto.getConversacionId()));
            return dto.getConversacionId();
        }

        ConversacionDb nueva = new ConversacionDb();
        nueva.setUsuarioId(dto.getUsuarioId());
        nueva.setSeccionTematica(dto.getSeccionTematica());
        nueva.setTitulo("Nueva conversación"); // ✅ Título temporal, se sobreescribe con IA
        nueva.setEstado(EstadoConversacion.ACTIVA);
        nueva.setFechaCreacion(LocalDateTime.now());
        return conversacionRepository.save(nueva).getId();
    }

    private MensajeDTO toDTO(MensajeDb db) {
        return new MensajeDTO(
                db.getId(),
                db.getConversacionId(),
                db.getTipo(),
                db.getContenido(),
                db.getChunksUtilizados(),
                db.getFecha());
    }
}