# DTOs

Los DTOs son `record` y viven en [application/dto](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto). Usan tipos simples (`UUID`, `String`, `int`, `BigDecimal`) para no exponer el dominio hacia afuera; el caso de uso los convierte en value objects.

## CrearPublicacionRequest

Entrada del caso de uso **CrearPublicacion**. [Ver código](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto/request/CrearPublicacionRequest.java)

| Campo | Tipo | Por qué es necesario |
|---|---|---|
| `vendedorId` | `UUID` | Saber qué vendedor es dueño de la publicación. |
| `nombreProducto` | `String` | Lo que ve el comprador en el anuncio. |
| `descripcion` | `String` | Obligatoria: explica el producto al comprador (material, tamaño, para qué sirve). No puede venir vacía ni superar 500 caracteres. |
| `especieDestino` | `String` | Regla 1: solo PERRO o GATO. Se convierte con `EspecieDestino.desde(...)`. |
| `montoPrecio` | `BigDecimal` | Regla 2: precio mayor a cero. |
| `moneda` | `String` | Junto con el monto forma el value object `Dinero`. |
| `stockInicial` | `int` | Unidades disponibles al publicar; debe ser al menos 1. |
| `categoria` | `String` | Clasifica el producto (ALIMENTO, JUGUETE, ACCESORIO, HIGIENE, SALUD). |

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
