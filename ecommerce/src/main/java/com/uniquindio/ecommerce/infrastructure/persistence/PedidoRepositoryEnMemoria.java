package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacion en memoria del repositorio de pedidos. Por ahora usamos un HashMap,
 * mas adelante se puede cambiar por una base de datos sin tocar el dominio.
 */
public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<PedidoId, Pedido> pedidos = new HashMap<>();

    @Override
    public void guardar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    @Override
    public Optional<Pedido> buscarPorId(PedidoId id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public List<Pedido> listar() {
        return new ArrayList<>(pedidos.values());
    }
}
