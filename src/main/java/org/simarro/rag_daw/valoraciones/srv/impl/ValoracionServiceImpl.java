package org.simarro.rag_daw.valoraciones.srv.impl;

import java.util.List;

import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.simarro.rag_daw.model.db.UsuarioDb;
import org.simarro.rag_daw.repository.UsuarioRepository;
import org.simarro.rag_daw.valoraciones.model.db.ValoracionDb;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionCreateDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionDTO;
import org.simarro.rag_daw.valoraciones.model.dto.ValoracionResumenDTO;
import org.simarro.rag_daw.valoraciones.repository.ValoracionRepository;
import org.simarro.rag_daw.valoraciones.srv.ValoracionService;
import org.simarro.rag_daw.valoraciones.srv.mapper.ValoracionMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValoracionServiceImpl implements ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ValoracionMapper valoracionMapper;

    @Override
    public ValoracionDTO crearValoracion(ValoracionCreateDTO dto, String emailUsuario) {

        UsuarioDb usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        ValoracionDb.TipoValoracion tipoValoracion;

        try {
            tipoValoracion = ValoracionDb.TipoValoracion.valueOf(dto.getValoracion().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("La valoración debe ser POSITIVA o NEGATIVA");
        }

        ValoracionDb valoracion = valoracionRepository
                .findByMensajeIdAndUsuarioId(dto.getMensajeId(), usuario.getId())
                .orElse(new ValoracionDb());

        valoracion.setMensajeId(dto.getMensajeId());
        valoracion.setUsuarioId(usuario.getId());
        valoracion.setValoracion(tipoValoracion);
        valoracion.setComentario(dto.getComentario());

        return valoracionMapper.toDTO(valoracionRepository.save(valoracion));
    }

    @Override
    public List<ValoracionDTO> getValoracionesMensaje(Long mensajeId) {
        return valoracionRepository.findByMensajeId(mensajeId)
                .stream()
                .map(valoracionMapper::toDTO)
                .toList();
    }

    @Override
    public ValoracionResumenDTO getResumenConversacion(Long conversacionId) {
        throw new UnsupportedOperationException(
                "Pendiente de implementar cuando exista relación entre valoraciones y conversación");
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
}