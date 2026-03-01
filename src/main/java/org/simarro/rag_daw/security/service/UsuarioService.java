package org.simarro.rag_daw.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;
import org.simarro.rag_daw.model.db.UsuarioDb;
import org.simarro.rag_daw.repository.UsuarioRepository;

import java.util.Optional;

@Service
@Transactional //Mantiene la coherencia de la BD si hay varios accesos de escritura concurrentes
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public Optional<UsuarioDb> getByNickname(String nickname){
        return usuarioRepository.findByEmail(nickname);
    }

    public boolean existsByNickname(String nickname){
        return usuarioRepository.existsByEmail(nickname);
    }

    public boolean existsByEmail(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public void save(@NonNull UsuarioDb usuario){
        usuarioRepository.save(usuario);
    }

    public Optional<UsuarioDb> getByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}