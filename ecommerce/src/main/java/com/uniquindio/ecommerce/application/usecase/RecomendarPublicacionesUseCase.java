package com.uniquindio.ecommerce.application.usecase;

import com.uniquindio.ecommerce.domain.entity.LineaPedido;
import com.uniquindio.ecommerce.domain.entity.Publicacion;
import com.uniquindio.ecommerce.domain.repository.PedidoRepository;
import com.uniquindio.ecommerce.domain.repository.PublicacionRepository;
import com.uniquindio.ecommerce.domain.valueobject.CompradorId;
import com.uniquindio.ecommerce.domain.valueobject.EspecieDestino;
import com.uniquindio.ecommerce.domain.valueobject.EstadoPedido;
import com.uniquindio.ecommerce.domain.valueobject.PublicacionId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Funcionalidad propia: "Recomendados para tu mascota".
 * Mira lo que el comprador ya compro (pedidos CONFIRMADO, ENVIADO o ENTREGADO), saca la especie para la
 * que mas unidades compra (perro o gato; si empatan, las dos) y recomienda publicaciones de esa especie
 * que todavia no ha comprado y que se pueden pedir. Si aun no ha comprado nada, no hay recomendaciones.
 * Repositories: PedidoRepository y PublicacionRepository.
 */
@Service
@RequiredArgsConstructor
public class RecomendarPublicacionesUseCase {

    private static final Set<EstadoPedido> YA_COMPRADOS =
            Set.of(EstadoPedido.CONFIRMADO, EstadoPedido.ENVIADO, EstadoPedido.ENTREGADO);

    /** Cuantas publicaciones por especie se revisan para armar las recomendaciones. */
    private static final int REVISAR_POR_ESPECIE = 50;

    private final PedidoRepository pedidoRepository;
    private final PublicacionRepository publicacionRepository;

    @Transactional(readOnly = true)
    public List<Publicacion> ejecutar(CompradorId compradorId, int limite) {
        List<LineaPedido> comprado = pedidoRepository.buscarPorComprador(compradorId).stream()
                .filter(pedido -> YA_COMPRADOS.contains(pedido.getEstado()))
                .flatMap(pedido -> pedido.getLineas().stream())
                .toList();
        if (comprado.isEmpty()) {
            return List.of();
        }

        Map<EspecieDestino, Integer> unidadesPorEspecie = new EnumMap<>(EspecieDestino.class);
        comprado.forEach(linea ->
                unidadesPorEspecie.merge(linea.getEspecieDestino(), linea.getCantidad().valor(), Integer::sum));
        int maximo = Collections.max(unidadesPorEspecie.values());

        Set<PublicacionId> yaComprados = comprado.stream()
                .map(LineaPedido::getPublicacionId)
                .collect(Collectors.toSet());

        return unidadesPorEspecie.entrySet().stream()
                .filter(especie -> especie.getValue() == maximo)
                .flatMap(especie -> publicacionRepository
                        .buscarCatalogo(null, especie.getKey(), null, 0, REVISAR_POR_ESPECIE)
                        .contenido().stream())
                .filter(Publicacion::aceptaPedidos)
                .filter(publicacion -> !yaComprados.contains(publicacion.getId()))
                .limit(limite)
                .toList();
    }
}
