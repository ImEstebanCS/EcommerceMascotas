package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Pedido;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.PedidoId;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.PedidoPersistenceMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementacion real del repositorio de pedidos con JPA. Guarda y carga el agregado completo
 * desde su raiz: el pedido con todas sus lineas.
 */
@Repository
@RequiredArgsConstructor
public class PedidoRepositoryJpa implements PedidoRepository {

    private final PedidoJpaDataRepository dataRepository;
    private final PedidoPersistenceMapper mapper;

    @Override
    public void guardar(Pedido pedido) {
        dataRepository.save(mapper.toEntity(pedido));
    }

    @Override
    public Optional<Pedido> buscarPorId(PedidoId id) {
        return dataRepository.findById(id.valor()).map(mapper::toDomain);
    }

    @Override
    public List<Pedido> listar() {
        return dataRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Pedido> buscarPorComprador(CompradorId compradorId) {
        return dataRepository.findByCompradorId(compradorId.valor()).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
