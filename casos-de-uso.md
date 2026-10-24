# Casos de uso

Cada caso de uso solo orquesta: busca en los repositorios, llama a los métodos de las entidades y guarda. Las reglas de negocio están dentro de las entidades y los value objects.

| # | Caso de uso | Rol | Repository que necesita | Estado |
|---|---|---|---|---|
| 1 | **CrearPublicacion** | Vendedor | `PublicacionRepository` | Implementado: [CrearPublicacionUseCase.java](ecommerce/src/main/java/com/uniquindio/ecommerce/application/usecase/CrearPublicacionUseCase.java) |
| 2 | **ActualizarStockPublicacion** | Vendedor | `PublicacionRepository` | Implementado: [ActualizarStockPublicacionUseCase.java](ecommerce/src/main/java/com/uniquindio/ecommerce/application/usecase/ActualizarStockPublicacionUseCase.java) |
| 3 | **RetirarPublicacion** | Vendedor | `PublicacionRepository` | Implementado: [RetirarPublicacionUseCase.java](ecommerce/src/main/java/com/uniquindio/ecommerce/application/usecase/RetirarPublicacionUseCase.java) |
| 4 | **CrearPedido** | Comprador | `PedidoRepository`, `PublicacionRepository` | Implementado: [CrearPedidoUseCase.java](ecommerce/src/main/java/com/uniquindio/ecommerce/application/usecase/CrearPedidoUseCase.java) |
| 5 | **AgregarLineaAPedido** | Comprador | `PedidoRepository`, `PublicacionRepository` | Documentado |
| 6 | **ConfirmarPedido** | Comprador | `PedidoRepository`, `PublicacionRepository` | Implementado: [ConfirmarPedidoUseCase.java](ecommerce/src/main/java/com/uniquindio/ecommerce/application/usecase/ConfirmarPedidoUseCase.java) |
| 7 | **ModificarPedidoAntesDeDespacho** | Comprador | `PedidoRepository`, `PublicacionRepository` | Documentado |
| 8 | **CancelarPedido** | Comprador | `PedidoRepository`, `PublicacionRepository` | Implementado: [CancelarPedidoUseCase.java](ecommerce/src/main/java/com/uniquindio/ecommerce/application/usecase/CancelarPedidoUseCase.java) |

Implementados: 6 (3 del vendedor y 3 del comprador). Documentados: 2 (AgregarLineaAPedido y ModificarPedidoAntesDeDespacho).

## Detalle

### 1. CrearPublicacion (vendedor)
Recibe un [CrearPublicacionRequest](dtos.md#crearpublicacionrequest), crea la `Publicacion`, la publica y la guarda. Falla si la especie no es PERRO o GATO, si el precio no es mayor a cero o si el stock inicial es 0.

### 2. ActualizarStockPublicacion (vendedor)
Busca la publicación, llama a `actualizarStock(nuevoStock)` y la guarda. Si el stock queda en 0 pasa a AGOTADA; si una AGOTADA recibe stock vuelve a PUBLICADA. No aplica a publicaciones retiradas. Si la publicación no existe lanza `NoSuchElementException`.

### 3. RetirarPublicacion (vendedor)
Busca la publicación, llama a `retirar()` y la guarda. Desde ese momento no recibe pedidos ni se puede volver a publicar. No se borra: queda en estado RETIRADA (eliminación lógica).

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
Busca el pedido y llama a `cancelar()`. El propio pedido decide qué stock hay que devolver: si ya estaba CONFIRMADO devuelve sus líneas, y si estaba en BORRADOR no devuelve ninguna (todavía no había descontado nada). Por cada línea, el caso de uso busca la publicación y llama a `devolverStock(cantidad)`; una publicación AGOTADA vuelve a PUBLICADA. Un pedido ya enviado o entregado no se puede cancelar.
