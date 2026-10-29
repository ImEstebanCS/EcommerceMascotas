package com.uniquindio.ecommerce.application.dto.response;

import com.uniquindio.ecommerce.domain.repository.Pagina;

import java.util.List;
import java.util.function.Function;

/**
 * Respuesta de un listado paginado: el contenido de la pagina, el total de elementos
 * y el total de paginas.
 */
public record PageResponse<T>(
        List<T> contenido,
        long totalElementos,
        int totalPaginas,
        int pagina,
        int tamano
) {
    public static <D, T> PageResponse<T> desde(Pagina<D> pagina, Function<D, T> convertidor) {
        return new PageResponse<>(
                pagina.contenido().stream().map(convertidor).toList(),
                pagina.totalElementos(),
                pagina.totalPaginas(),
                pagina.numero(),
                pagina.tamano());
    }
}
