package com.uniquindio.ecommerce.infrastructure.security;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

/**
 * Saca del token el id de quien hace la peticion (claim "uid"). Los controllers lo usan para que
 * el comprador o el vendedor salga del token y no del body: nadie puede actuar a nombre de otro.
 */
public final class UsuarioAutenticado {

    private UsuarioAutenticado() {
    }

    public static UUID id(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("uid"));
    }
}
