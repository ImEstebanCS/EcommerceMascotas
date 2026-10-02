# Casos de uso

Cada caso de uso solo orquesta: busca en los repositorios, llama a los métodos de las entidades y guarda. Las reglas de negocio están dentro de las entidades y los value objects.

| # | Caso de uso | Rol | Repository que necesita | Estado |
|---|---|---|---|---|
| 1 | **CrearPublicacion** | Vendedor | `PublicacionRepository` | Implementado: [CrearPublicacion.java](ecommerce/src/main/java/com/uniquindio/ecommerce/application/publicacion/CrearPublicacion.java) |
| 2 | **ActualizarStockPublicacion** | Vendedor | `PublicacionRepository` | Documentado |
| 3 | **RetirarPublicacion** | Vendedor | `PublicacionRepository` | Documentado |
| 4 | **CrearPedido** | Comprador | `PedidoRepository`, `PublicacionRepository` | Implementado: [CrearPedido.java](ecommerce/src/main/java/com/uniquindio/ecommerce/application/pedido/CrearPedido.java) |
| 5 | **AgregarLineaAPedido** | Comprador | `PedidoRepository`, `PublicacionRepository` | Documentado |
| 6 | **ConfirmarPedido** | Comprador | `PedidoRepository`, `PublicacionRepository` | Implementado: [ConfirmarPedido.java](ecommerce/src/main/java/com/uniquindio/ecommerce/application/pedido/ConfirmarPedido.java) |
| 7 | **ModificarPedidoAntesDeDespacho** | Comprador | `PedidoRepository`, `PublicacionRepository` | Documentado |
| 8 | **CancelarPedido** | Comprador | `PedidoRepository` | Documentado |

## Detalle

### 1. CrearPublicacion (vendedor)
Recibe un [CrearPublicacionRequest](dtos.md#crearpublicacionrequest), crea la `Publicacion`, la publica y la guarda. Falla si la especie no es PERRO o GATO, si el precio no es mayor a cero o si el stock inicial es 0.

### 2. ActualizarStockPublicacion (vendedor)
Busca la publicación, llama a `actualizarStock(nuevoStock)` y la guarda. Si el stock queda en 0 pasa a AGOTADA; si una AGOTADA recibe stock vuelve a PUBLICADA. No aplica a publicaciones retiradas.

### 3. RetirarPublicacion (vendedor)
Busca la publicación, llama a `retirar()` y la guarda. Desde ese momento no recibe pedidos ni se puede volver a publicar.

### 4. CrearPedido (comprador)
Recibe un [CrearPedidoRequest](dtos.md#crearpedidorequest), crea el `Pedido` en BORRADOR y por cada línea busca la publicación y llama a `agregarLinea(publicacion, cantidad)`. Guarda el pedido.

### 5. AgregarLineaAPedido (comprador)
Busca el pedido y la publicación, llama a `pedido.agregarLinea(...)` y guarda el pedido. El total se recalcula solo.

### 6. ConfirmarPedido (comprador)
Busca el pedido y las publicaciones de sus líneas, llama a `pedido.confirmar(publicaciones)` (valida que tenga líneas y que ninguna supere el stock), descuenta el stock de cada publicación y guarda todo. Devuelve un [PedidoResponse](dtos.md#pedidoresponse).

### 7. ModificarPedidoAntesDeDespacho (comprador)
Caso de uso: modificar un pedido antes de que sea despachado.

Busca el pedido y la publicación y llama a `cambiarCantidad(...)` o `quitarLinea(...)`. Solo se puede mientras el pedido no esté ENVIADO, ENTREGADO o CANCELADO; la nueva cantidad tampoco puede superar el stock.

### 8. CancelarPedido (comprador)
Busca el pedido, llama a `cancelar()` y lo guarda. Un pedido ya enviado o entregado no se puede cancelar.
