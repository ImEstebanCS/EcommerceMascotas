# Reto de diseño

## Actores

- **Comprador:** persona que tiene un perro o un gato y arma pedidos con las publicaciones disponibles. Puede crear, modificar, confirmar y cancelar sus pedidos.
- **Vendedor:** tienda o persona que publica productos para mascotas. Crea publicaciones, actualiza su stock y puede retirarlas.

En esta entrega el comprador y el vendedor no son agregados: el dominio solo los referencia por su identidad ([CompradorId](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/shared/CompradorId.java) y [VendedorId](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/shared/VendedorId.java)).

## Reglas de negocio innegociables

| # | Regla | Dónde se protege |
|---|---|---|
| 1 | Solo se venden productos para **perros o gatos**; ninguna publicación puede tener otra especie destino. | `EspecieDestino.desde(...)` y `Publicacion.crear(...)` |
| 2 | El **precio** de toda publicación debe ser **mayor a cero**. | `Publicacion.crear(...)` |
| 3 | Un pedido **no puede confirmarse** si alguna línea supera el **stock** disponible de su publicación. | `Pedido.confirmar(...)` |
| 4 | Un pedido **enviado, entregado o cancelado** no puede modificar sus productos ni cantidades. | `Pedido.validarQueSePuedeModificar()` |
| 5 | Una publicación **retirada** por el vendedor no puede recibir nuevos pedidos ni volver a publicarse. | `Publicacion.publicar()` y `Publicacion.aceptaPedidos()` |

Toda violación de estas reglas lanza [ReglaDominioException](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/shared/ReglaDominioException.java).
