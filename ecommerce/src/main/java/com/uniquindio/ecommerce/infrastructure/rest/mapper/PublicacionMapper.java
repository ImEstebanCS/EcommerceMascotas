package com.uniquindio.ecommerce.infrastructure.rest.mapper;

import com.uniquindio.ecommerce.application.dto.request.CrearPublicacionRequest;
import com.uniquindio.ecommerce.application.dto.response.PublicacionResponse;
import com.uniquindio.ecommerce.application.usecase.CrearPublicacionUseCase.NuevaPublicacion;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.Dinero;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;

import org.springframework.stereotype.Component;

/**
 * Convierte entre los DTOs de la API de publicaciones y los tipos del dominio, en los dos sentidos.
 * Si un valor rompe una regla del negocio (por ejemplo, una especie que no es PERRO ni GATO),
 * el value object lanza ReglaDominioException y la API responde 409.
 */
@Component
public class PublicacionMapper {

    public NuevaPublicacion toNuevaPublicacion(VendedorId vendedorId, CrearPublicacionRequest request) {
        return new NuevaPublicacion(
                vendedorId,
                request.nombreProducto(),
                request.descripcion(),
                EspecieDestino.desde(request.especieDestino()),
                new Dinero(request.montoPrecio(), request.moneda()),
                new Stock(request.stockInicial()),
                CategoriaProducto.desde(request.categoria()));
    }

    public PublicacionResponse toResponse(Publicacion publicacion) {
        return new PublicacionResponse(
                publicacion.getId().valor(),
                publicacion.getVendedorId().valor(),
                publicacion.getNombreProducto(),
                publicacion.getDescripcion(),
                publicacion.getEspecieDestino().name(),
                publicacion.getCategoria().name(),
                publicacion.getPrecio().monto(),
                publicacion.getPrecio().moneda(),
                publicacion.getStock().unidades(),
                publicacion.getEstado().name());
    }
}
