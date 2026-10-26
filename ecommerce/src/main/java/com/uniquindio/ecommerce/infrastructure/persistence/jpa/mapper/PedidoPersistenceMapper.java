package com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper;

import com.uniquindio.ecommerce.domain.entity.LineaPedido;
import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.valueobject.Cantidad;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.DineroEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.LineaPedidoEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PedidoEntity;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Convierte el agregado Pedido completo (raiz + lineas) a sus entidades JPA y de vuelta.
 */
@Component
public class PedidoPersistenceMapper {

    public PedidoEntity toEntity(Pedido pedido) {
        PedidoEntity entity = new PedidoEntity();
        entity.setId(pedido.getId().valor());
        entity.setCompradorId(pedido.getCompradorId().valor());
        entity.setEstado(pedido.getEstado());
        entity.setTotal(DineroEmbeddable.desde(pedido.getTotal()));

        List<LineaPedido> lineas = pedido.getLineas();
        for (int i = 0; i < lineas.size(); i++) {
            LineaPedido linea = lineas.get(i);
            LineaPedidoEntity lineaEntity = new LineaPedidoEntity();
            lineaEntity.setId(linea.getId());
            lineaEntity.setPedido(entity);
            lineaEntity.setPublicacionId(linea.getPublicacionId().valor());
            lineaEntity.setEspecieDestino(linea.getEspecieDestino());
            lineaEntity.setCantidad(linea.getCantidad().valor());
            lineaEntity.setPrecioUnitario(DineroEmbeddable.desde(linea.getPrecioUnitario()));
            lineaEntity.setSubtotal(DineroEmbeddable.desde(linea.getSubtotal()));
            lineaEntity.setPosicion(i);
            entity.getLineas().add(lineaEntity);
        }
        return entity;
    }

    public Pedido toDomain(PedidoEntity entity) {
        List<LineaPedido> lineas = new ArrayList<>();
        entity.getLineas().stream()
                .sorted(Comparator.comparingInt(LineaPedidoEntity::getPosicion))
                .forEach(linea -> lineas.add(LineaPedido.reconstruir(
                        linea.getId(),
                        new PublicacionId(linea.getPublicacionId()),
                        linea.getEspecieDestino(),
                        new Cantidad(linea.getCantidad()),
                        linea.getPrecioUnitario().aDominio())));

        return Pedido.reconstruir(
                new PedidoId(entity.getId()),
                new CompradorId(entity.getCompradorId()),
                lineas,
                entity.getEstado());
    }
}
