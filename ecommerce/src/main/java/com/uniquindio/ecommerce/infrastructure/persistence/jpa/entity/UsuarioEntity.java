package com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Tabla "usuarios". Es un detalle de seguridad (quien inicia sesion y con que rol), por eso vive
 * en infraestructura y no en el dominio. El id es el mismo VendedorId o CompradorId del dominio.
 */
@Entity
@Table(name = "usuarios")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /** Hash BCrypt de la contrasena; la contrasena en texto plano nunca se guarda. */
    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;
}
