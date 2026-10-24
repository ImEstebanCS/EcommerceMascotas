package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.application.dto.request.CrearPublicacionRequest;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.Dinero;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;

/**
 * Caso de uso del vendedor: crear una publicacion y dejarla publicada.
 * Repository: PublicacionRepository.
 */
public class CrearPublicacionUseCase {

    private final PublicacionRepository publicacionRepository;

    public CrearPublicacionUseCase(PublicacionRepository publicacionRepository) {
        this.publicacionRepository = publicacionRepository;
    }

    public PublicacionId ejecutar(CrearPublicacionRequest request) {
        Publicacion publicacion = Publicacion.crear(
                PublicacionId.nuevo(),
                new VendedorId(request.vendedorId()),
                request.nombreProducto(),
                EspecieDestino.desde(request.especieDestino()),
                new Dinero(request.montoPrecio(), request.moneda()),
                new Stock(request.stockInicial()),
                CategoriaProducto.desde(request.categoria()));

        publicacion.publicar();
        publicacionRepository.guardar(publicacion);
        return publicacion.getId();
    }
}
