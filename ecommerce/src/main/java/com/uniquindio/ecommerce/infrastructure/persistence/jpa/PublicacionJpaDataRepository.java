package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.repository.PublicacionVentas;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPedido;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPublicacion;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PublicacionEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio de Spring Data. Los metodos simples los escribe Hibernate leyendo el nombre;
 * las consultas del catalogo y del ranking van en JPQL con parametros con nombre, para que el
 * filtro, el conteo y la paginacion los haga la base de datos y no Java.
 */
interface PublicacionJpaDataRepository extends JpaRepository<PublicacionEntity, UUID> {

    List<PublicacionEntity> findByEspecieDestino(EspecieDestino especieDestino);

    @Query("""
            SELECT p FROM PublicacionEntity p
            WHERE p.estado <> :retirada
              AND (LOWER(p.nombreProducto) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')))
              AND p.especieDestino IN :especies
              AND p.categoria IN :categorias
            """)
    Page<PublicacionEntity> buscarCatalogo(@Param("texto") String texto,
                                           @Param("especies") Collection<EspecieDestino> especies,
                                           @Param("categorias") Collection<CategoriaProducto> categorias,
                                           @Param("retirada") EstadoPublicacion retirada,
                                           Pageable pageable);

    @Query("""
            SELECT new com.uniquindio.ecommerce.domain.repository.PublicacionVentas(
                       p.id, p.nombreProducto, SUM(l.cantidad))
            FROM PublicacionEntity p
            JOIN LineaPedidoEntity l ON l.publicacionId = p.id
            JOIN l.pedido ped
            WHERE ped.estado IN :estadosVendidos
            GROUP BY p.id, p.nombreProducto
            ORDER BY SUM(l.cantidad) DESC, p.nombreProducto ASC
            """)
    List<PublicacionVentas> masVendidas(@Param("estadosVendidos") Collection<EstadoPedido> estadosVendidos,
                                        Pageable pageable);
}
