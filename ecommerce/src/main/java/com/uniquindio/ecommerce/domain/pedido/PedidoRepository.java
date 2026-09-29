package com.uniquindio.ecommerce.domain.pedido;

import com.uniquindio.ecommerce.domain.shared.PedidoId;

import java.util.List;
import java.util.Optional;

/**
 * El dominio dice QUE necesita guardar y buscar. El COMO lo decide infraestructura.
 */
public interface PedidoRepository {

    void guardar(Pedido pedido);

    Optional<Pedido> buscarPorId(PedidoId id);

    List<Pedido> listar();
}
