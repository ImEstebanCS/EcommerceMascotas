# DTOs

Los DTOs son `record` y viven en [application/dto](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto). Usan tipos simples (`UUID`, `String`, `int`, `BigDecimal`) para no exponer el dominio hacia afuera; el caso de uso los convierte en value objects.

## CrearPublicacionRequest

Entrada del caso de uso **CrearPublicacion**. [Ver código](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto/CrearPublicacionRequest.java)

| Campo | Tipo | Por qué es necesario |
|---|---|---|
| `vendedorId` | `UUID` | Saber qué vendedor es dueño de la publicación. |
| `nombreProducto` | `String` | Lo que ve el comprador en el anuncio. |
| `especieDestino` | `String` | Regla 1: solo PERRO o GATO. Se convierte con `EspecieDestino.desde(...)`. |
| `montoPrecio` | `BigDecimal` | Regla 2: precio mayor a cero. |
| `moneda` | `String` | Junto con el monto forma el value object `Dinero`. |
| `stockInicial` | `int` | Unidades disponibles al publicar; debe ser al menos 1. |
| `categoria` | `String` | Clasifica el producto (ALIMENTO, JUGUETE, ACCESORIO, HIGIENE, SALUD). |

## CrearPedidoRequest

Entrada del caso de uso **CrearPedido**. [Ver código](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto/CrearPedidoRequest.java)

| Campo | Tipo | Por qué es necesario |
|---|---|---|
| `compradorId` | `UUID` | Saber quién hace el pedido. |
| `lineas` | `List<LineaPedidoRequest>` | Los productos que se piden; un pedido confirmado necesita al menos uno. |
| `lineas[].publicacionId` | `UUID` | Para buscar la publicación y tomar su precio, especie y stock. |
| `lineas[].cantidad` | `int` | Unidades pedidas; debe ser mayor a cero y no superar el stock. |

El precio no viene en el request a propósito: lo toma el dominio de la publicación, así el comprador no puede mandar un precio inventado.

## PedidoResponse

Salida del caso de uso **ConfirmarPedido**. [Ver código](ecommerce/src/main/java/com/uniquindio/ecommerce/application/dto/PedidoResponse.java)

| Campo | Tipo | Por qué es necesario |
|---|---|---|
| `pedidoId` | `UUID` | Para que el comprador pueda consultar el pedido después. |
| `estado` | `String` | Confirma que el pedido quedó CONFIRMADO. |
| `montoTotal` | `BigDecimal` | Lo que el comprador va a pagar (suma de subtotales). |
| `moneda` | `String` | Para mostrar el total correctamente. |
| `lineas` | `List<LineaResponse>` | Resumen de lo que se confirmó. |
| `lineas[].publicacionId` | `UUID` | Qué publicación se compró. |
| `lineas[].cantidad` | `int` | Cuántas unidades. |
| `lineas[].subtotal` | `BigDecimal` | Precio unitario por cantidad de esa línea. |
