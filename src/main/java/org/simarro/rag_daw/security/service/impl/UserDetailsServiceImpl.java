package org.simarro.rag_daw.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.simarro.rag_daw.model.db.UsuarioDb;
import org.simarro.rag_daw.security.entity.UsuarioPrincipal;
import org.simarro.rag_daw.security.service.UsuarioService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Método que debemos sobreescribir (debe tener este nombre) de la interfaz
        // UserDetailsService.
        // En nuestro caso buscamos por nickname en la BD y devolvemos un
        // UsuarioPrincipal,
        // que es una implementación de la interfaz UserDetails.
        // Usamos orElseThrow para manejar la ausencia del usuario en una sola línea
        // limpia.
        UsuarioDb usuario = usuarioService.getByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        return UsuarioPrincipal.build(usuario);
    }
}
