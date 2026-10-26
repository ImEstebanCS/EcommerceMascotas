package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PublicacionEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio de Spring Data: Hibernate escribe el SQL leyendo el nombre de cada metodo.
 */
interface PublicacionJpaDataRepository extends JpaRepository<PublicacionEntity, UUID> {

    List<PublicacionEntity> findByEspecieDestino(EspecieDestino especieDestino);
}
