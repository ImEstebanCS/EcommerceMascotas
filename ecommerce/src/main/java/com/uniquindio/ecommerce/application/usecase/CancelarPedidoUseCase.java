package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.LineaPedido;
import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Caso de uso del comprador: cancelar un pedido y devolver a cada publicacion
 * las unidades que el pedido ya habia descontado. Devuelve el Pedido cancelado.
 * Un comprador solo cancela sus pedidos: si el pedido es de otro, responde como si no existiera.
 * Repositories: PedidoRepository y PublicacionRepository.
 */
@Service
@RequiredArgsConstructor
public class CancelarPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PublicacionRepository publicacionRepository;

    @Transactional
    public Pedido ejecutar(PedidoId pedidoId, CompradorId compradorId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId)
                .filter(encontrado -> encontrado.getCompradorId().equals(compradorId))
                .orElseThrow(() -> new NoSuchElementException("El pedido " + pedidoId.valor() + " no existe"));

        // el Pedido decide si hay stock que devolver (solo si ya estaba CONFIRMADO)
        List<LineaPedido> lineasAReponer = pedido.cancelar();

        for (LineaPedido linea : lineasAReponer) {
            Publicacion publicacion = publicacionRepository.buscarPorId(linea.getPublicacionId())
                    .orElseThrow(() -> new NoSuchElementException("La publicacion " + linea.getPublicacionId().valor() + " no existe"));
            publicacion.devolverStock(linea.getCantidad());
            publicacionRepository.guardar(publicacion);
        }
        pedidoRepository.guardar(pedido);
        return pedido;
    }
}
