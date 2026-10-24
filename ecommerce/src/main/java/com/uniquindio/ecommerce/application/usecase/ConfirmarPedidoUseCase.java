package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.dto.response.PedidoResponse;
import com.uniquindio.ecommerce.domain.entity.LineaPedido;
import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Caso de uso del comprador: confirmar un pedido y descontar el stock de sus publicaciones.
 * Repositories: PedidoRepository y PublicacionRepository.
 */
public class ConfirmarPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PublicacionRepository publicacionRepository;

    public ConfirmarPedidoUseCase(PedidoRepository pedidoRepository, PublicacionRepository publicacionRepository) {
        this.pedidoRepository = pedidoRepository;
        this.publicacionRepository = publicacionRepository;
    }

    public PedidoResponse ejecutar(UUID pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(new PedidoId(pedidoId))
                .orElseThrow(() -> new NoSuchElementException("El pedido " + pedidoId + " no existe"));

        List<Publicacion> publicaciones = new ArrayList<>();
        for (LineaPedido linea : pedido.getLineas()) {
            Publicacion publicacion = publicacionRepository.buscarPorId(linea.getPublicacionId())
                    .orElseThrow(() -> new NoSuchElementException("La publicacion " + linea.getPublicacionId().valor() + " no existe"));
            publicaciones.add(publicacion);
        }

        pedido.confirmar(publicaciones);

        for (int i = 0; i < publicaciones.size(); i++) {
            Publicacion publicacion = publicaciones.get(i);
            publicacion.descontarStock(pedido.getLineas().get(i).getCantidad());
            publicacionRepository.guardar(publicacion);
        }
        pedidoRepository.guardar(pedido);

        return PedidoResponse.desde(pedido);
    }
}
