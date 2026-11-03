package com.uniquindio.ecommerce.infrastructure.persistence.jpa;

import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;
import com.uniquindio.ecommerce.infrastructure.persistence.jpa.mapper.PublicacionPersistenceMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementacion real del repositorio del dominio: guarda las publicaciones en la base de datos
 * con JPA. La interfaz vive en domain (QUE se necesita); esta clase en infrastructure (COMO).
 */
@Repository
@RequiredArgsConstructor
public class PublicacionRepositoryJpa implements PublicacionRepository {

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
}
