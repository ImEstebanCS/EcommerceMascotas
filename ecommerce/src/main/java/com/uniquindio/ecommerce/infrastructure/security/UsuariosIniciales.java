package com.uniquindio.ecommerce.infrastructure.security;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.UsuarioJpaDataRepository;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.RolUsuario;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.UsuarioEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Usuarios de prueba que se crean al arrancar. Sus ids son los mismos de los vendedores y compradores
 * de data.sql, asi cada uno ve sus publicaciones y pedidos. Las contrasenas se guardan con BCrypt.
 */
@Component
@RequiredArgsConstructor
public class UsuariosIniciales implements CommandLineRunner {

    private record Semilla(String id, String nombre, String email, String clave, RolUsuario rol) {
    }

    private final UsuarioJpaDataRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        List<Semilla> semillas = List.of(
                new Semilla("a1a1a1a1-0000-4000-8000-000000000001", "Tienda Patitas", "patitas@mascotas.com", "vendedor123", RolUsuario.VENDEDOR),
                new Semilla("a1a1a1a1-0000-4000-8000-000000000002", "Michi Store", "michi@mascotas.com", "vendedor123", RolUsuario.VENDEDOR),
                new Semilla("c1c1c1c1-0000-4000-8000-000000000001", "Ana", "ana@mascotas.com", "comprador123", RolUsuario.COMPRADOR),
                new Semilla("c1c1c1c1-0000-4000-8000-000000000002", "Bruno", "bruno@mascotas.com", "comprador123", RolUsuario.COMPRADOR),
                new Semilla("c1c1c1c1-0000-4000-8000-000000000003", "Carla", "carla@mascotas.com", "comprador123", RolUsuario.COMPRADOR));

        for (Semilla semilla : semillas) {
            if (!repository.existsByEmail(semilla.email())) {
                repository.save(new UsuarioEntity(UUID.fromString(semilla.id()), semilla.nombre(), semilla.email(),
                        encoder.encode(semilla.clave()), semilla.rol()));
            }
        }
    }
}
