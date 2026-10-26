package com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.DineroEmbeddable;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PublicacionEntity;

import org.springframework.stereotype.Component;

/**
 * Convierte entre la entidad de dominio Publicacion y la entidad JPA PublicacionEntity.
 */
@Component
public class PublicacionPersistenceMapper {

    public PublicacionEntity toEntity(Publicacion publicacion) {
        PublicacionEntity entity = new PublicacionEntity();
        entity.setId(publicacion.getId().valor());
        entity.setVendedorId(publicacion.getVendedorId().valor());
        entity.setNombreProducto(publicacion.getNombreProducto());
        entity.setDescripcion(publicacion.getDescripcion());
        entity.setEspecieDestino(publicacion.getEspecieDestino());
        entity.setPrecio(DineroEmbeddable.desde(publicacion.getPrecio()));
        entity.setStock(publicacion.getStock().unidades());
        entity.setCategoria(publicacion.getCategoria());
        entity.setEstado(publicacion.getEstado());
        return entity;
    }

    public Publicacion toDomain(PublicacionEntity entity) {
        return Publicacion.reconstruir(
                new PublicacionId(entity.getId()),
                new VendedorId(entity.getVendedorId()),
                entity.getNombreProducto(),
                entity.getDescripcion(),
                entity.getEspecieDestino(),
                entity.getPrecio().aDominio(),
                new Stock(entity.getStock()),
                entity.getCategoria(),
                entity.getEstado());
    }
}
