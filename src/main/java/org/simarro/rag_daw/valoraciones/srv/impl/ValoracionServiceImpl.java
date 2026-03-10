package org.simarro.rag_daw.valoraciones.srv.impl;

import lombok.RequiredArgsConstructor;
import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.simarro.rag_daw.model.db.UsuarioDb;
import org.simarro.rag_daw.repository.UsuarioRepository;
import org.simarro.rag_daw.valoraciones.model.db.ValoracionDb;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionResumenDTO;
import org.simarro.rag_daw.valoraciones.repository.ValoracionRepository;
import org.simarro.rag_daw.valoraciones.srv.ValoracionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValoracionServiceImpl implements ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public ValoracionDTO crearValoracion(ValoracionCreateDTO dto, String emailUsuario) {
        UsuarioDb usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        ValoracionDb.TipoValoracion tipoValoracion;
        try {
            tipoValoracion = ValoracionDb.TipoValoracion.valueOf(dto.getValoracion().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("La valoración debe ser POSITIVA o NEGATIVA");
        }

        ValoracionDb valoracion = valoracionRepository
                .findByMensajeIdAndUsuarioId(dto.getMensajeId(), usuario.getId())
                .orElse(new ValoracionDb());

        valoracion.setMensajeId(dto.getMensajeId());
        valoracion.setUsuarioId(usuario.getId());
        valoracion.setValoracion(tipoValoracion);
        valoracion.setComentario(dto.getComentario());

        return mapToDTO(valoracionRepository.save(valoracion));
    }

    @Override
    public List<ValoracionDTO> getValoracionesMensaje(Long mensajeId) {
        return valoracionRepository.findByMensajeId(mensajeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ValoracionResumenDTO getResumenConversacion(Long conversacionId) {
        List<ValoracionDb> valoraciones = valoracionRepository.findByConversacionId(conversacionId);

        long positivas = valoraciones.stream()
                .filter(v -> v.getValoracion() == ValoracionDb.TipoValoracion.POSITIVA)
                .count();

        long negativas = valoraciones.size() - positivas;
        long total = valoraciones.size();
        double ratio = total == 0 ? 0.0 : (double) positivas / total;

        List<ValoracionDTO> detalles = valoraciones.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new ValoracionResumenDTO(positivas, negativas, ratio, detalles);
    }

    @Override
    public void eliminarValoracion(Long id, String emailUsuario) {
        UsuarioDb usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        ValoracionDb valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoración no encontrada"));

        if (!valoracion.getUsuarioId().equals(usuario.getId())) {
            throw new SecurityException("No puedes eliminar valoraciones de otros usuarios");
        }

        valoracionRepository.delete(valoracion);
    }

    private ValoracionDTO mapToDTO(ValoracionDb entity) {
        return new ValoracionDTO(
                entity.getId(),
                entity.getMensajeId(),
                entity.getUsuarioId(),
                entity.getValoracion().name(),
                entity.getComentario(),
                entity.getFechaCreacion()
        );
    }
}