package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.dto.request.CrearPedidoRequest;
import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.Cantidad;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import java.util.NoSuchElementException;

/**
 * Caso de uso del comprador: crear un pedido en borrador con sus lineas.
 * Repositories: PedidoRepository y PublicacionRepository.
 */
public class CrearPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PublicacionRepository publicacionRepository;

    public CrearPedidoUseCase(PedidoRepository pedidoRepository, PublicacionRepository publicacionRepository) {
        this.pedidoRepository = pedidoRepository;
        this.publicacionRepository = publicacionRepository;
    }

    public PedidoId ejecutar(CrearPedidoRequest request) {
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), new CompradorId(request.compradorId()));

        for (CrearPedidoRequest.LineaPedidoRequest linea : request.lineas()) {
            Publicacion publicacion = publicacionRepository.buscarPorId(new PublicacionId(linea.publicacionId()))
                    .orElseThrow(() -> new NoSuchElementException("La publicacion " + linea.publicacionId() + " no existe"));
            pedido.agregarLinea(publicacion, new Cantidad(linea.cantidad()));
        }

        pedidoRepository.guardar(pedido);
        return pedido.getId();
    }
}
