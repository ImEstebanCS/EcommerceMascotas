package com.uniquindio.ecommerce.application.pedido;

import com.uniquindio.ecommerce.application.dto.CrearPedidoRequest;
import com.uniquindio.ecommerce.domain.pedido.Pedido;
import com.uniquindio.ecommerce.domain.pedido.PedidoRepository;
import com.uniquindio.ecommerce.domain.publicacion.Publicacion;
import com.uniquindio.ecommerce.domain.publicacion.PublicacionRepository;
import com.uniquindio.ecommerce.domain.shared.Cantidad;
import com.uniquindio.ecommerce.domain.shared.CompradorId;
import com.uniquindio.ecommerce.domain.shared.PedidoId;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;
import com.uniquindio.ecommerce.domain.shared.ReglaDominioException;

/**
 * Caso de uso del comprador: crear un pedido en borrador con sus lineas.
 * Repositories: PedidoRepository y PublicacionRepository.
 */
public class CrearPedido {

    private final PedidoRepository pedidoRepository;
    private final PublicacionRepository publicacionRepository;

    public CrearPedido(PedidoRepository pedidoRepository, PublicacionRepository publicacionRepository) {
        this.pedidoRepository = pedidoRepository;
        this.publicacionRepository = publicacionRepository;
    }

    public PedidoId ejecutar(CrearPedidoRequest request) {
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), new CompradorId(request.compradorId()));

        for (CrearPedidoRequest.LineaPedidoRequest linea : request.lineas()) {
            Publicacion publicacion = publicacionRepository.buscarPorId(new PublicacionId(linea.publicacionId()))
                    .orElseThrow(() -> new ReglaDominioException("La publicacion " + linea.publicacionId() + " no existe"));
            pedido.agregarLinea(publicacion, new Cantidad(linea.cantidad()));
        }

        pedidoRepository.guardar(pedido);
        return pedido.getId();
    }
}
