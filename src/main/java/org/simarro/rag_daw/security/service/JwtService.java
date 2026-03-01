package org.simarro.rag_daw.security.service;

import java.util.function.Function;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface JwtService {
 public String generateToken(Authentication authentication);
 public String getNicknameUsuarioFromToken(String token);
 public <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
 public boolean isTokenValid(String token, UserDetails userDetails);
}
