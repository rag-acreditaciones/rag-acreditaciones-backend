package org.simarro.rag_daw.security.security.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import org.simarro.rag_daw.security.service.JwtService;
import org.simarro.rag_daw.security.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  // Se ejecutará en cada petición de la API Rest (por heredar de OncePerRequestFilter)
  // y comprobará que sea válido el token utilizando el provider

  private final JwtService jwtService;
  private final UserDetailsServiceImpl userDetailsService;

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    final String authHeader = req.getHeader("Authorization");
    final String jwt;
    final String nickname;
    // Comprueba cabecera
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(req, res);
      return;
    }
    jwt = authHeader.substring(7);
    try { //Hay token y lo procesamos
      nickname = jwtService.getNicknameUsuarioFromToken(jwt);
      // Comprueba si el token es valido para permitir el acceso al recurso
      if (nickname != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(nickname);
        if (jwtService.isTokenValid(jwt, userDetails)) {// token valido
          // Obtenemos el UserNamePasswordAuthenticationToken en base al userDetails y sus
          // autorizaciones
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
          SecurityContextHolder.getContext().setAuthentication(authToken);// aplicamos autorización al contexto
        }
      }
    } catch (MalformedJwtException e) {
      logger.error("Token mal formado");
    } catch (UnsupportedJwtException e) {
      logger.error("Token no soportado");
    } catch (ExpiredJwtException e) {
      logger.error("Token expirado");
    } catch (IllegalArgumentException e) {
      logger.error("Token vacío");
    } catch (SecurityException e) {
      logger.error("Fallo en la firma");
    }

    filterChain.doFilter(req, res);
  }
}
