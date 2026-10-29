# DTOs

Los DTOs son `record` y viven en [application/dto](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto). Usan tipos simples (`UUID`, `String`, `int`, `BigDecimal`) para no exponer el dominio hacia afuera. Los casos de uso no los reciben: los mappers de la API (`PublicacionMapper`, `PedidoMapper`) los convierten en value objects antes de llamarlos.

Las validaciones del DTO (`@NotNull`, `@NotBlank`, `@Min`) revisan que la petición venga completa y bien escrita, y si falla responden **400**. Las reglas del negocio las revisa el dominio, y si se rompen responden **409**.

## CrearPublicacionRequest

Body de `POST /api/publicaciones` (caso de uso **CrearPublicacion**). [Ver código](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto/request/CrearPublicacionRequest.java)

| Campo | Tipo | Validación del DTO (400) | Por qué es necesario |
|---|---|---|---|
| `vendedorId` | `UUID` | `@NotNull` | Saber qué vendedor es dueño de la publicación. |
| `nombreProducto` | `String` | `@NotBlank` | Lo que ve el comprador en el anuncio. |
| `descripcion` | `String` | `@NotBlank` | Obligatoria: explica el producto al comprador (material, tamaño, para qué sirve). Que no supere 500 caracteres lo revisa el dominio (409). |
| `especieDestino` | `String` | `@NotBlank` | Regla 1: solo PERRO o GATO. Se convierte con `EspecieDestino.desde(...)`; otra especie responde 409. |
| `montoPrecio` | `BigDecimal` | `@NotNull` | Regla 2: precio mayor a cero (lo revisa el dominio, 409). |
| `moneda` | `String` | `@NotBlank` | Junto con el monto forma el value object `Dinero`. |
| `stockInicial` | `int` | — | Unidades disponibles al publicar; con 0 no se puede publicar (409). |
| `categoria` | `String` | `@NotBlank` | Clasifica el producto (ALIMENTO, JUGUETE, ACCESORIO, HIGIENE, SALUD). |

## ActualizarStockRequest

Body de `PUT /api/publicaciones/{id}/stock` (caso de uso **ActualizarStockPublicacion**). [Ver código](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto/request/ActualizarStockRequest.java)

| Campo | Tipo | Validación del DTO (400) | Por qué es necesario |
|---|---|---|---|
| `stock` | `Integer` | `@NotNull` | Las unidades nuevas. Es `Integer` y no `int` para que, si no viene, responda 400 en vez de poner el stock en 0 sin avisar. Un número negativo lo rechaza el value object `Stock` (409). |

## PublicacionResponse

Respuesta de los endpoints de publicaciones (crear, cambiar stock, retirar y consultar). La arma `PublicacionMapper`. [Ver código](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto/response/PublicacionResponse.java)

| Campo | Tipo | Por qué es necesario |
|---|---|---|
| `id` | `UUID` | Para consultar, cambiar el stock o retirar la publicación. |
| `vendedorId` | `UUID` | Quién la vende. |
| `nombreProducto`, `descripcion` | `String` | Lo que ve el comprador. |
| `especieDestino`, `categoria` | `String` | Para filtrar y mostrar el producto. |
| `precio`, `moneda` | `BigDecimal`, `String` | El precio real de la publicación (nada quemado). |
| `stock` | `int` | Unidades disponibles. |
| `estado` | `String` | PUBLICADA, AGOTADA o RETIRADA. |

## CrearPedidoRequest

Body de `POST /api/pedidos` (caso de uso **CrearPedido**). [Ver código](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto/request/CrearPedidoRequest.java)

| Campo | Tipo | Validación (400 si falla) | Por qué es necesario |
|---|---|---|---|
| `compradorId` | `UUID` | `@NotNull` | Saber quién hace el pedido. |
| `lineas` | `List<LineaPedidoRequest>` | `@NotEmpty` | Los productos que se piden; un pedido sin líneas no se puede confirmar. |
| `lineas[].publicacionId` | `UUID` | `@NotNull` | Para buscar la publicación y tomar su precio, especie y stock. |
| `lineas[].cantidad` | `int` | `@Min(1)` | Unidades pedidas. Que no superen el stock lo revisa el dominio (409). |

El precio no viene en el request a propósito: lo toma el dominio de la publicación, así el comprador no puede mandar un precio inventado.

El caso de uso no recibe este DTO: `PedidoMapper` lo convierte en `CompradorId` y una lista de `LineaSolicitada(PublicacionId, Cantidad)`.

## PedidoResponse

Respuesta de todos los endpoints de pedidos (crear, confirmar, cancelar y consultar). La arma `PedidoMapper` a partir del `Pedido` del dominio. [Ver código](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto/response/PedidoResponse.java)

| Campo | Tipo | Por qué es necesario |
|---|---|---|
| `pedidoId` | `UUID` | Para que el comprador pueda consultar, confirmar o cancelar el pedido después. |
| `compradorId` | `UUID` | De quién es el pedido. |
| `estado` | `String` | En qué va el pedido: BORRADOR, CONFIRMADO, ENVIADO, ENTREGADO o CANCELADO. |
| `montoTotal` | `BigDecimal` | Lo que el comprador va a pagar (suma de subtotales). |
| `moneda` | `String` | Para mostrar el total correctamente. |
| `lineas` | `List<LineaResponse>` | Lo que tiene el pedido. |
| `lineas[].publicacionId` | `UUID` | Qué publicación se pidió. |
| `lineas[].cantidad` | `int` | Cuántas unidades. |
| `lineas[].precioUnitario` | `BigDecimal` | El precio que tenía la publicación cuando se agregó al pedido. |
| `lineas[].subtotal` | `BigDecimal` | Precio unitario por cantidad de esa línea. |
