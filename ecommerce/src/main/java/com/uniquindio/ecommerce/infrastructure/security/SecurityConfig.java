package com.uniquindio.ecommerce.infrastructure.security;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.UsuarioJpaDataRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Reglas de acceso de la API. Aqui se decide que rutas son publicas y que rol necesita cada una.
 * La API es stateless: no guarda sesion, cada peticion trae su token y se revisa por separado.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final ManejadorErroresSeguridad manejadorErrores;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // sin sesion ni cookies no hay CSRF que proteger: el token viaja en el header Authorization
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                // la consola de H2 se dibuja dentro de frames
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/error", "/api-docs/**", "/swagger-ui/**",
                                "/swagger-ui.html", "/h2-console/**").permitAll()
                        // el catalogo lo puede ver cualquiera
                        .requestMatchers(HttpMethod.GET, "/api/publicaciones/**").permitAll()
                        // solo el vendedor publica, cambia el stock y retira
                        .requestMatchers(HttpMethod.POST, "/api/publicaciones").hasAuthority("VENDEDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/publicaciones/*/stock").hasAuthority("VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/publicaciones/*").hasAuthority("VENDEDOR")
                        // solo el comprador crea, confirma, cancela y consulta pedidos
                        .requestMatchers("/api/pedidos/**").hasAuthority("COMPRADOR")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter))
                        .authenticationEntryPoint(manejadorErrores)
                        .accessDeniedHandler(manejadorErrores))
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint(manejadorErrores)
                        .accessDeniedHandler(manejadorErrores))
                .build();
    }

    /** Las contrasenas se guardan con BCrypt: un hash lento y con sal, que no se puede revertir. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Como encuentra Spring Security al usuario que intenta iniciar sesion: por su correo. */
    @Bean
    public UserDetailsService userDetailsService(UsuarioJpaDataRepository usuarioRepository) {
        return email -> usuarioRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con ese correo"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /** Deja que el frontend en Angular (http://localhost:4200) llame a la API desde el navegador. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
