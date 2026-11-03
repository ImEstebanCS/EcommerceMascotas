package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.Cantidad;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Caso de uso del comprador: crear un pedido en borrador con sus lineas.
 * Recibe tipos del dominio, no el DTO: la conversion la hace el mapper de la API.
 * Repositories: PedidoRepository y PublicacionRepository.
 */
@Service
@RequiredArgsConstructor
public class CrearPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PublicacionRepository publicacionRepository;

    @Transactional
    public Pedido ejecutar(CompradorId compradorId, List<LineaSolicitada> lineas) {
        Pedido pedido = Pedido.crear(PedidoId.nuevo(), compradorId);

        for (LineaSolicitada linea : lineas) {
            Publicacion publicacion = publicacionRepository.buscarPorId(linea.publicacionId())
                    .orElseThrow(() -> new NoSuchElementException("La publicacion " + linea.publicacionId().valor() + " no existe"));
            pedido.agregarLinea(publicacion, linea.cantidad());
        }

        pedidoRepository.guardar(pedido);
        return pedido;
    }

    /** Lo que pide el comprador en cada linea: que publicacion y cuantas unidades. */
    public record LineaSolicitada(PublicacionId publicacionId, Cantidad cantidad) {
    }
}
