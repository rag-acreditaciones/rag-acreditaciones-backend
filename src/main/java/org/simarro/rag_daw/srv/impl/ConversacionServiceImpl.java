package org.simarro.rag_daw.srv.impl;

import java.util.List;

import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.simarro.rag_daw.model.db.ConversacionDb;
import org.simarro.rag_daw.model.db.MensajeDb;
import org.simarro.rag_daw.model.dto.ConversacionCreateDTO;
import org.simarro.rag_daw.model.dto.ConversacionDetailDTO;
import org.simarro.rag_daw.model.dto.ConversacionResponseDTO;
import org.simarro.rag_daw.model.enums.EstadoConversacion;
import org.simarro.rag_daw.repository.ConversacionRepository;
import org.simarro.rag_daw.repository.MensajeRepository;
import org.simarro.rag_daw.srv.ConversacionService;
import org.simarro.rag_daw.srv.mapper.ConversacionMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversacionServiceImpl implements ConversacionService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final ConversacionMapper mapper;

    public ConversacionServiceImpl(
            ConversacionRepository conversacionRepository,
            MensajeRepository mensajeRepository,
            ConversacionMapper mapper) {

        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ConversacionDetailDTO crearConversacion(ConversacionCreateDTO dto) {

        ConversacionDb conversacion = mapper.toEntity(dto);

        ConversacionDb saved = conversacionRepository.save(conversacion);

        ConversacionDetailDTO response = mapper.toDetailDTO(saved);
        response.setListaMensajes(List.of());

        return response;
    }

    @Override
    public Page<ConversacionResponseDTO> listarConversaciones(
            Long usuarioId,
            String seccionTematica,
            EstadoConversacion estado,
            Pageable pageable) {

        Specification<ConversacionDb> spec = (root, query, cb) -> cb.equal(root.get("usuarioId"), usuarioId);

        if (seccionTematica != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("seccionTematica"), seccionTematica));
        }

        if (estado != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado));
        }

        Page<ConversacionDb> resultado =
                conversacionRepository.findAll(spec, pageable);

        return resultado.map(conversacion -> {

            ConversacionResponseDTO dto = mapper.toResponseDTO(conversacion);

            mensajeRepository.findTopByConversacionIdOrderByFechaDesc(conversacion.getId()).ifPresent(dto::setUltimoMensaje);

            return dto;
        });
    }

    @Override
    public ConversacionDetailDTO obtenerConversacion(Long id) {

        ConversacionDb conversacion = findConversacionOrThrow(id);

        ConversacionDetailDTO dto = mapper.toDetailDTO(conversacion);

        List<MensajeDb> mensajes = mensajeRepository.findByConversacionIdOrderByFechaAsc(id);

        dto.setListaMensajes(mensajes);

        return dto;
    }

    @Override
    @Transactional
    public void archivarConversacion(Long id, Long usuarioId) {

        ConversacionDb conversacion = findConversacionOrThrow(id);

        if (!conversacion.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para archivar esta conversacion");
        }

        conversacion.setEstado(EstadoConversacion.ARCHIVADA);

        conversacionRepository.save(conversacion);
    }

    @Override
    @Transactional
    public void eliminarConversacion(Long id, Long usuarioId) {

        ConversacionDb conversacion = findConversacionOrThrow(id);

        if (!conversacion.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta conversacion");
        }

        conversacionRepository.delete(conversacion);
    }

    private ConversacionDb findConversacionOrThrow(Long id) {
        return conversacionRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Conversacion no encontrada con id: " + id));
    }

    private String generarTitulo(String texto) {

    if (texto == null || texto.isBlank()) {
        return "Nueva conversación";
    }

    String limpio = texto.trim();

    if (limpio.length() <= 50) {
        return limpio;
    }

    return limpio.substring(0, 50) + "...";
}
}