package com.uniquindio.ecommerce.application.pedido;

import com.uniquindio.ecommerce.application.dto.PedidoResponse;
import com.uniquindio.ecommerce.domain.pedido.LineaPedido;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.domain.pedido.PedidoRepository;
import com.uniquindio.ecommerce.domain.publicacion.Publicacion;
import com.uniquindio.ecommerce.domain.publicacion.PublicacionRepository;
import com.uniquindio.ecommerce.domain.shared.PedidoId;
import com.uniquindio.ecommerce.domain.shared.ReglaDominioException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso del comprador: confirmar un pedido y descontar el stock de sus publicaciones.
 * Repositories: PedidoRepository y PublicacionRepository.
 */
public class ConfirmarPedido {

    private final PedidoRepository pedidoRepository;
    private final PublicacionRepository publicacionRepository;

    public ConfirmarPedido(PedidoRepository pedidoRepository, PublicacionRepository publicacionRepository) {
        this.pedidoRepository = pedidoRepository;
        this.publicacionRepository = publicacionRepository;
    }

    public PedidoResponse ejecutar(UUID pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(new PedidoId(pedidoId))
                .orElseThrow(() -> new ReglaDominioException("El pedido " + pedidoId + " no existe"));

        List<Publicacion> publicaciones = new ArrayList<>();
        for (LineaPedido linea : pedido.getLineas()) {
            Publicacion publicacion = publicacionRepository.buscarPorId(linea.getPublicacionId())
                    .orElseThrow(() -> new ReglaDominioException("La publicacion de una linea no existe"));
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
