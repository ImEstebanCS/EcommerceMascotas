package com.uniquindio.ecommerce.application.publicacion;

import com.uniquindio.ecommerce.application.dto.CrearPublicacionRequest;
import com.uniquindio.ecommerce.domain.publicacion.CategoriaProducto;
import com.uniquindio.ecommerce.domain.publicacion.Publicacion;
import com.uniquindio.ecommerce.domain.publicacion.PublicacionRepository;
import com.uniquindio.ecommerce.domain.shared.Dinero;
import com.uniquindio.ecommerce.domain.shared.EspecieDestino;
import com.uniquindio.ecommerce.domain.shared.PublicacionId;
import com.uniquindio.ecommerce.domain.shared.Stock;
import com.uniquindio.ecommerce.domain.shared.VendedorId;

/**
 * Caso de uso del vendedor: crear una publicacion y dejarla publicada.
 * Repository: PublicacionRepository.
 */
public class CrearPublicacion {

    private final PublicacionRepository publicacionRepository;

    public CrearPublicacion(PublicacionRepository publicacionRepository) {
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
