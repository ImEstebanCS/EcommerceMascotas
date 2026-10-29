package com.uniquindio.ecommerce.domain.repository;

import java.util.UUID;

/**
 * Una fila del ranking de publicaciones mas vendidas: cuantas unidades se vendieron de cada una
 * (sumando las lineas de pedidos confirmados, enviados o entregados).
 */
public record PublicacionVentas(UUID publicacionId, String nombreProducto, Long unidadesVendidas) {
}
