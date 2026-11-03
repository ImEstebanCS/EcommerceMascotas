package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

/**
 * Rol de seguridad del usuario. Viaja dentro del token y decide que rutas puede usar.
 */
public enum RolUsuario {
    COMPRADOR,
    VENDEDOR
}
