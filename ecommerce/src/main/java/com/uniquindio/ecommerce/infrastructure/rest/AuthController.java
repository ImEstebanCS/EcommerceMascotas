package com.uniquindio.ecommerce.infrastructure.rest;

import com.uniquindio.ecommerce.application.dto.request.LoginRequest;
import com.uniquindio.ecommerce.application.dto.request.RegistroRequest;
import com.uniquindio.ecommerce.application.dto.response.TokenResponse;
import com.uniquindio.ecommerce.application.dto.response.UsuarioResponse;
import com.uniquindio.ecommerce.infrastructure.security.AutenticacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Registro e inicio de sesion. Son las unicas rutas de escritura que no piden token.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AutenticacionService autenticacionService;

    /** Crea una cuenta de COMPRADOR o VENDEDOR. */
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest request) {
        return autenticacionService.registrar(request);
    }

    /** Devuelve el token JWT que se pega en el boton Authorize de Swagger. */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return autenticacionService.login(request);
    }
}
