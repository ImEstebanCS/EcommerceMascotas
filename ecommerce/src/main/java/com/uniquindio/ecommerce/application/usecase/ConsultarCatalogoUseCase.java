package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionVentas;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Consultas del catalogo (para cualquier visitante): listado paginado con busqueda y filtros,
 * detalle de una publicacion y ranking de las mas vendidas.
 * Aqui solo se validan las entradas (pagina, tamano, limite); las reglas de negocio viven en el dominio.
 * Repository: PublicacionRepository.
 */
@Service
@RequiredArgsConstructor
public class ConsultarCatalogoUseCase {

    public static final int TAMANO_MAXIMO = 50;
    public static final int RANKING_MAXIMO = 20;

    private final PublicacionRepository publicacionRepository;

    @Transactional(readOnly = true)
    public Pagina<Publicacion> buscar(String texto, EspecieDestino especie, CategoriaProducto categoria,
                                      int pagina, int tamano) {
        if (pagina < 0) {
            throw new IllegalArgumentException("La pagina no puede ser negativa");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("El tamano de la pagina debe estar entre 1 y " + TAMANO_MAXIMO);
        }
        return publicacionRepository.buscarCatalogo(texto, especie, categoria, pagina, tamano);
    }

    @Transactional(readOnly = true)
    public Publicacion obtener(PublicacionId publicacionId) {
        return publicacionRepository.buscarPorId(publicacionId)
                .orElseThrow(() -> new NoSuchElementException("La publicacion " + publicacionId.valor() + " no existe"));
    }

    @Transactional(readOnly = true)
    public List<PublicacionVentas> masVendidas(int limite) {
        if (limite < 1 || limite > RANKING_MAXIMO) {
            throw new IllegalArgumentException("El limite del ranking debe estar entre 1 y " + RANKING_MAXIMO);
        }
        return publicacionRepository.masVendidas(limite);
    }
}
