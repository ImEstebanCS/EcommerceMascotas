package com.uniquindio.ecommerce.infrastructure.persistence;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionVentas;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPublicacion;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacion en memoria del repositorio de publicaciones usando un HashMap.
 * La aplicacion usa PublicacionRepositoryJpa; esta clase queda para pruebas sin base de datos
 * (por eso no lleva @Repository: si lo tuviera, Spring encontraria dos implementaciones).
 */
public class PublicacionRepositoryEnMemoria implements PublicacionRepository {

    private final Map<PublicacionId, Publicacion> publicaciones = new HashMap<>();

    @Override
    public void guardar(Publicacion publicacion) {
        publicaciones.put(publicacion.getId(), publicacion);
    }

    @Override
    public Optional<Publicacion> buscarPorId(PublicacionId id) {
        return Optional.ofNullable(publicaciones.get(id));
    }

    @Override
    public List<Publicacion> listarPorEspecie(EspecieDestino especie) {
        return publicaciones.values().stream()
                .filter(p -> p.getEspecieDestino() == especie)
                .toList();
    }

    /** En memoria el filtro se hace en Java; la version JPA lo hace en la base de datos. */
    @Override
    public Pagina<Publicacion> buscarCatalogo(String texto, EspecieDestino especie, CategoriaProducto categoria,
                                              int pagina, int tamano) {
        String patron = texto == null ? "" : texto.trim().toLowerCase();
        List<Publicacion> filtradas = publicaciones.values().stream()
                .filter(p -> p.getEstado() != EstadoPublicacion.RETIRADA)
                .filter(p -> p.getNombreProducto().toLowerCase().contains(patron)
                        || p.getDescripcion().toLowerCase().contains(patron))
                .filter(p -> especie == null || p.getEspecieDestino() == especie)
                .filter(p -> categoria == null || p.getCategoria() == categoria)
                .sorted(Comparator.comparing(Publicacion::getNombreProducto))
                .toList();
        int desde = Math.min(pagina * tamano, filtradas.size());
        int hasta = Math.min(desde + tamano, filtradas.size());
        int totalPaginas = (int) Math.ceil((double) filtradas.size() / tamano);
        return new Pagina<>(filtradas.subList(desde, hasta), filtradas.size(), totalPaginas, pagina, tamano);
    }

    /** Este repositorio no conoce los pedidos, asi que no puede calcular ventas. */
    @Override
    public List<PublicacionVentas> masVendidas(int limite) {
        return List.of();
    }
}
