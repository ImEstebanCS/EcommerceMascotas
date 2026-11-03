package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Consultas del comprador sobre sus pedidos: los pedidos de un comprador y el detalle de uno.
 * Repository: PedidoRepository.
 */
@Service
@RequiredArgsConstructor
public class ConsultarPedidosUseCase {

    private final PedidoRepository pedidoRepository;

    @Transactional(readOnly = true)
    public List<Pedido> deComprador(CompradorId compradorId) {
        return pedidoRepository.buscarPorComprador(compradorId);
    }

    /** Un comprador solo ve sus pedidos: si el pedido es de otro, responde como si no existiera. */
    @Transactional(readOnly = true)
    public Pedido obtener(PedidoId pedidoId, CompradorId compradorId) {
        return pedidoRepository.buscarPorId(pedidoId)
                .filter(encontrado -> encontrado.getCompradorId().equals(compradorId))
                .orElseThrow(() -> new NoSuchElementException("El pedido " + pedidoId.valor() + " no existe"));
    }
}
