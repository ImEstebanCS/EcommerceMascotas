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

Además hay dos casos de uso de solo lectura: `ConsultarCatalogoUseCase` (catálogo, detalle y ranking de publicaciones) y `ConsultarPedidosUseCase` (pedidos de un comprador y detalle de un pedido).

Los casos de uso reciben tipos del dominio (`VendedorId`, `CompradorId`, `PublicacionId`, `PedidoId`, `Stock`, `Cantidad`...) y devuelven la entidad (`Publicacion` o `Pedido`), no DTOs. Los mappers de la API (`PublicacionMapper` y `PedidoMapper`) convierten el DTO en esos tipos antes de llamarlos, y la entidad en el DTO de respuesta. Así la capa de aplicación no depende de HTTP.

El `VendedorId` o `CompradorId` que reciben sale del token de quien hace la petición. Por eso los casos de uso que cambian o consultan algo existente buscan por id **y** por dueño: si la publicación es de otro vendedor, o el pedido es de otro comprador, responden `NoSuchElementException` (404), como si no existiera.

## Detalle

### 1. CrearPublicacion (vendedor)
Recibe una `NuevaPublicacion` con tipos del dominio (el mapper la arma a partir del [CrearPublicacionRequest](dtos.md#crearpublicacionrequest)), crea la `Publicacion`, la publica, la guarda y la devuelve. Falla si falta la descripción, si la especie no es PERRO o GATO, si el precio no es mayor a cero o si el stock inicial es 0.

### 2. ActualizarStockPublicacion (vendedor)
Recibe el `PublicacionId` y el nuevo `Stock`, busca la publicación, llama a `actualizarStock(nuevoStock)`, la guarda y la devuelve. Si el stock queda en 0 pasa a AGOTADA; si una AGOTADA recibe stock vuelve a PUBLICADA. No aplica a publicaciones retiradas. Si la publicación no existe lanza `NoSuchElementException`.

### 3. RetirarPublicacion (vendedor)
Busca la publicación, llama a `retirar()`, la guarda y la devuelve. La API lo expone como `DELETE /api/publicaciones/{id}`. Desde ese momento no recibe pedidos ni se puede volver a publicar. No se borra: queda en estado RETIRADA (eliminación lógica).

### 4. CrearPedido (comprador)
Recibe el `CompradorId` y las líneas pedidas como `LineaSolicitada(PublicacionId, Cantidad)`; el mapper las arma a partir del [CrearPedidoRequest](dtos.md#crearpedidorequest). Crea el `Pedido` en BORRADOR, por cada línea busca la publicación y llama a `agregarLinea(publicacion, cantidad)`, guarda el pedido y lo devuelve. Si se pide más de lo que hay en stock o la publicación no acepta pedidos, el dominio lanza `ReglaDominioException` y no se guarda nada.

### 5. AgregarLineaAPedido (comprador)
Busca el pedido y la publicación, llama a `pedido.agregarLinea(...)` y guarda el pedido. El total se recalcula solo.

### 6. ConfirmarPedido (comprador)
Busca el pedido y las publicaciones de sus líneas, llama a `pedido.confirmar(publicaciones)` (valida que tenga líneas y que ninguna supere el stock), descuenta el stock de cada publicación y guarda todo. Devuelve el `Pedido`, que la API convierte en [PedidoResponse](dtos.md#pedidoresponse).

### 7. ModificarPedidoAntesDeDespacho (comprador)
Caso de uso: modificar un pedido antes de que sea despachado.

Busca el pedido y la publicación y llama a `cambiarCantidad(...)` o `quitarLinea(...)`. Solo se puede mientras el pedido no esté ENVIADO, ENTREGADO o CANCELADO; la nueva cantidad tampoco puede superar el stock.

### 8. CancelarPedido (comprador)
Busca el pedido y llama a `cancelar()`. El propio pedido decide qué stock hay que devolver: si ya estaba CONFIRMADO devuelve sus líneas, y si estaba en BORRADOR no devuelve ninguna (todavía no había descontado nada). Por cada línea, el caso de uso busca la publicación y llama a `devolverStock(cantidad)`; una publicación AGOTADA vuelve a PUBLICADA. Un pedido ya enviado o entregado no se puede cancelar. Devuelve el `Pedido` cancelado.
