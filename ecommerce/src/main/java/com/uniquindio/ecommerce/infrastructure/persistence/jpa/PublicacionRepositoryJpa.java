package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.Pagina;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionVentas;
import com.uniquindio.ecommerce.domain.valueobject.CategoriaProducto;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPedido;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPublicacion;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.entity.PublicacionEntity;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.PublicacionPersistenceMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementacion real del repositorio del dominio: guarda las publicaciones en la base de datos
 * con JPA. La interfaz vive en domain (QUE se necesita); esta clase en infrastructure (COMO).
 */
@Repository
@RequiredArgsConstructor
public class PublicacionRepositoryJpa implements PublicacionRepository {

    /** Estados en los que un pedido ya cuenta como venta (descontó stock y no se canceló). */
    private static final Set<EstadoPedido> ESTADOS_VENDIDOS =
            EnumSet.of(EstadoPedido.CONFIRMADO, EstadoPedido.ENVIADO, EstadoPedido.ENTREGADO);

    private final PublicacionJpaDataRepository dataRepository;
    private final PublicacionPersistenceMapper mapper;

    @Override
    public void guardar(Publicacion publicacion) {
        dataRepository.save(mapper.toEntity(publicacion));
    }

    @Override
    public Optional<Publicacion> buscarPorId(PublicacionId id) {
        return dataRepository.findById(id.valor()).map(mapper::toDomain);
    }

    @Override
    public List<Publicacion> listarPorEspecie(EspecieDestino especie) {
        return dataRepository.findByEspecieDestino(especie).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Pagina<Publicacion> buscarCatalogo(String texto, EspecieDestino especie, CategoriaProducto categoria,
                                              int pagina, int tamano) {
        String patron = texto == null ? "" : texto.trim();
        Set<EspecieDestino> especies = especie == null ? EnumSet.allOf(EspecieDestino.class) : EnumSet.of(especie);
        Set<CategoriaProducto> categorias = categoria == null ? EnumSet.allOf(CategoriaProducto.class) : EnumSet.of(categoria);

        Page<PublicacionEntity> resultado = dataRepository.buscarCatalogo(patron, especies, categorias,
                EstadoPublicacion.RETIRADA, PageRequest.of(pagina, tamano, Sort.by("nombreProducto")));

        // la conversion de Page (Spring) a Pagina (dominio) se hace aqui, en el adaptador
        return new Pagina<>(
                resultado.getContent().stream().map(mapper::toDomain).toList(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.getNumber(),
                resultado.getSize());
    }

    @Override
    public List<PublicacionVentas> masVendidas(int limite) {
        return dataRepository.masVendidas(ESTADOS_VENDIDOS, PageRequest.of(0, limite));
    }
}
