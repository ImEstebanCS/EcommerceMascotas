package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.Dinero;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.domain.valueobject.Stock;
import com.uniquindio.ecommerce.domain.valueobject.VendedorId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso del vendedor: crear una publicacion y dejarla publicada.
 * Recibe tipos del dominio, no el DTO: la conversion la hace el mapper de la API.
 * Repository: PublicacionRepository.
 */
@Service
@RequiredArgsConstructor
public class CrearPublicacionUseCase {

    private final PublicacionRepository publicacionRepository;

    @Transactional
    public Publicacion ejecutar(NuevaPublicacion datos) {
        Publicacion publicacion = Publicacion.crear(
                PublicacionId.nuevo(),
                datos.vendedorId(),
                datos.nombreProducto(),
                datos.descripcion(),
                datos.especieDestino(),
                datos.precio(),
                datos.stockInicial(),
                datos.categoria());

        publicacion.publicar();
        publicacionRepository.guardar(publicacion);
        return publicacion;
    }

    /** Los datos de la publicacion nueva, ya convertidos a tipos del dominio. */
    public record NuevaPublicacion(VendedorId vendedorId, String nombreProducto, String descripcion,
                                   EspecieDestino especieDestino, Dinero precio, Stock stockInicial,
                                   CategoriaProducto categoria) {
    }
}
