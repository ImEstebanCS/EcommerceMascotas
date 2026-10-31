package com.uniquindio.ecommerce.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Arma el token JWT: header.payload.firma.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtEncoder encoder;

    public String generar(String email, UUID usuarioId, String rol, Instant emitidoEn, Instant expiraEn) {
        // HEADER: con que algoritmo se firma
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        // PAYLOAD: quien es y que rol tiene. Cualquiera lo puede leer (es base64), por eso no lleva la contrasena;
        // pero si alguien lo cambia, la firma deja de coincidir y el token se rechaza.
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ecommerce-mascotas-api")
                .issuedAt(emitidoEn)
                .expiresAt(expiraEn)
                .subject(email)
                .claim("uid", usuarioId.toString())
                .claim("rol", rol)
                .build();

        // FIRMA: HMAC-SHA256 de header + payload con la clave secreta
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
