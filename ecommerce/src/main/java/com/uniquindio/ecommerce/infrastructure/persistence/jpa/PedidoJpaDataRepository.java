package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PedidoEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio de Spring Data para la tabla de pedidos (las lineas viajan con el pedido).
 */
interface PedidoJpaDataRepository extends JpaRepository<PedidoEntity, UUID> {

    /** Spring Data arma la consulta desde el nombre: WHERE comprador_id = ?. */
    List<PedidoEntity> findByCompradorId(UUID compradorId);
}
