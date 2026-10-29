package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.LineaPedido;
import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Caso de uso del comprador: confirmar un pedido y descontar el stock de sus publicaciones.
 * Devuelve el Pedido del dominio; la API lo convierte en su DTO de respuesta.
 * Repositories: PedidoRepository y PublicacionRepository.
 */
@Service
@RequiredArgsConstructor
public class ConfirmarPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PublicacionRepository publicacionRepository;

    @Transactional
    public Pedido ejecutar(PedidoId pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .orElseThrow(() -> new NoSuchElementException("El pedido " + pedidoId.valor() + " no existe"));

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
        return pedido;
    }
}
