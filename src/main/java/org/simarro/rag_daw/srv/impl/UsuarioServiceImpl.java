package org.simarro.rag_daw.srv.impl;

import java.util.Optional;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import org.simarro.rag_daw.model.db.UsuarioDb;
import org.simarro.rag_daw.model.dto.LoginUsuario;
import org.simarro.rag_daw.model.dto.UsuarioInfo;
import org.simarro.rag_daw.repository.UsuarioRepository;
import org.simarro.rag_daw.srv.UsuarioService;
import org.simarro.rag_daw.srv.mapper.UsuarioMapper;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<UsuarioInfo> getByNickname(@NonNull String nickname) {
        Optional<UsuarioDb> usuarioDb = usuarioRepository.findByEmail(nickname);
        if (usuarioDb.isPresent()) {
            return Optional.of(UsuarioMapper.INSTANCE.usuarioDbToUsuarioInfo(usuarioDb.get()));
        } else {
            return Optional.empty();
        }
    }

    public boolean existsByNickname(@NonNull String nickname) {
        return usuarioRepository.existsByEmail(nickname);
    }

    public boolean existsByEmail(@NonNull String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean comprobarLogin(@NonNull LoginUsuario loginUsuario) {
        Optional<UsuarioDb> usuarioDb = usuarioRepository.findByEmail(loginUsuario.getNickname());
        return usuarioDb.isPresent() && usuarioDb.get().getPassword().equals(loginUsuario.getPassword());
    }
}
