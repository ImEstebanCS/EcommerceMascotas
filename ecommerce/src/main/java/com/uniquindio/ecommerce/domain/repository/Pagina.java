package com.uniquindio.ecommerce.domain.repository;

import java.util.List;

/**
 * Una pagina de resultados. Es propia del dominio para que el dominio no dependa de la
 * paginacion de Spring: el adaptador de persistencia convierte el Page de Spring en esta Pagina.
 */
public record Pagina<T>(List<T> contenido, long totalElementos, int totalPaginas, int numero, int tamano) {
}
