package org.simarro.rag_daw.srv;

import java.util.Optional;
import org.springframework.lang.NonNull;

import org.simarro.rag_daw.model.dto.LoginUsuario;
import org.simarro.rag_daw.model.dto.UsuarioInfo;

public interface UsuarioService {
    public Optional<UsuarioInfo> getByNickname(@NonNull String nickname);
    public boolean existsByNickname(@NonNull String nickname);
    public boolean existsByEmail(@NonNull String email);
    public boolean comprobarLogin(@NonNull LoginUsuario loginUsuario);
}


