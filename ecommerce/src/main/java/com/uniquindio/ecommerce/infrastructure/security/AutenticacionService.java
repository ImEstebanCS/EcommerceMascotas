package com.uniquindio.ecommerce.infrastructure.security;

import com.uniquindio.ecommerce.application.dto.request.LoginRequest;
import com.uniquindio.ecommerce.application.dto.request.RegistroRequest;
import com.uniquindio.ecommerce.application.dto.response.TokenResponse;
import com.uniquindio.ecommerce.application.dto.response.UsuarioResponse;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.RolUsuario;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.UsuarioEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.UUID;

/**
 * Registro e inicio de sesion. La duracion del token sale de application.properties (jwt.expiracion-minutos).
 */
@Service
@RequiredArgsConstructor
public class AutenticacionService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioJpaDataRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiracion-minutos}")
    private long minutosDeVida;

    /** Crea la cuenta guardando el hash BCrypt de la contrasena, nunca la contrasena. */
    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {
        String email = normalizar(request.email());
        if (usuarioRepository.existsByEmail(email)) {
            throw new CorreoYaRegistradoException("Ya existe una cuenta con el correo " + email);
        }
        UsuarioEntity usuario = new UsuarioEntity(UUID.randomUUID(), request.nombre().trim(), email,
                passwordEncoder.encode(request.password()), RolUsuario.valueOf(request.rol()));
        usuarioRepository.save(usuario);
        return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol().name());
    }

    /** Revisa correo y contrasena; si coinciden, firma un token con el correo, el rol y el vencimiento. */
    public TokenResponse login(LoginRequest request) {
        // si la contrasena no coincide con el hash BCrypt, authenticate lanza BadCredentialsException (401)
        Authentication autenticacion = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizar(request.email()), request.password()));
        UsuarioEntity usuario = ((CustomUserDetails) autenticacion.getPrincipal()).usuario();

        Instant ahora = Instant.now();
        Instant expira = ahora.plus(minutosDeVida, ChronoUnit.MINUTES);
        String token = jwtTokenProvider.generar(usuario.getEmail(), usuario.getId(), usuario.getRol().name(), ahora, expira);
        return new TokenResponse(token, "Bearer", expira, usuario.getEmail(), usuario.getRol().name());
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
