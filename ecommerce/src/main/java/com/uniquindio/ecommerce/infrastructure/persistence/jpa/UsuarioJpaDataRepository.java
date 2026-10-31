package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.UsuarioEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio de Spring Data para la tabla de usuarios. Lo usa la seguridad para el login y el registro.
 */
public interface UsuarioJpaDataRepository extends JpaRepository<UsuarioEntity, UUID> {

    Optional<UsuarioEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
