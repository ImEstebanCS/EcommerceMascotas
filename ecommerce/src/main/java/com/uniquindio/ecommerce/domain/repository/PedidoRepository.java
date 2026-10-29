package com.uniquindio.ecommerce.domain.repository;

import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;

import java.util.List;
import java.util.Optional;

/**
 * El dominio dice QUE necesita guardar y buscar. El COMO lo decide infraestructura.
 */
public interface PedidoRepository {

    void guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(PedidoId id);

    List<Pedido> listar();

    List<Pedido> buscarPorComprador(CompradorId compradorId);
}
