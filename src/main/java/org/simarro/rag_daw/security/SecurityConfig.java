package org.simarro.rag_daw.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.simarro.rag_daw.security.security.jwt.JwtAuthenticationFilter;
import org.simarro.rag_daw.security.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    UserDetailsServiceImpl userDetailsService; // Convierte la clase UsuarioDb en UsuarioPrincipal (UserDetails)

    @Bean
    public PasswordEncoder passwordEncoder() {// permite cifrar la contraseña
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    private static final String[] WHITE_LIST_URL = {"/auth/**",
            "/api-docs/**",
            "/swagger-ui/**",
            "/webjars/**"};
            @Bean
            public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                // Permitir no estar autenticado en "/auth" y el resto obligar a autenticar
                // Comprobar el token en cada petición (jwtTokenFilter)
                http    
                        .cors(customizer->customizer.configurationSource(CorsConfigurationSource()))
                        .csrf(csrf -> csrf
                                .disable())
                        .authorizeHttpRequests(authRequest -> authRequest
                                .requestMatchers(WHITE_LIST_URL).permitAll()
                                .anyRequest().authenticated())
                        .sessionManagement(sessionManager -> sessionManager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
            }
        
            // CORS (Cross-origin resource sharing) : Mecanismo que permite que recursos con
            // acceso
            // restringido puedan ser utilizados desde fuera de la API, por ejemplo desde
            // Angular
            @Bean
            CorsConfigurationSource CorsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                // Configurar desde donde se puede invocar a la API
                configuration.setAllowedOrigins(List.of("http://localhost:8005", "http://localhost:4200"));
                configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH")); // Que métodos pueden utilizarse
                configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                configuration.setAllowCredentials(true);
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
            }
}
