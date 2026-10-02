# Clasificación: Entidad vs Value Object

Para decidir usamos tres pruebas:

- **Identidad:** ¿dos objetos con los mismos datos siguen siendo cosas distintas? Si sí, es entidad.
- **Reemplazo:** ¿puedo cambiar el objeto por otro con los mismos valores sin que nadie lo note? Si sí, es value object.
- **Ciclo de vida:** ¿el objeto cambia de estado con el tiempo y hay que seguirle la pista? Si sí, es entidad.

## Entidades

| Concepto | Identidad | Reemplazo | Ciclo de vida | Código |
|---|---|---|---|---|
| **Pedido** | Dos pedidos del mismo comprador con los mismos productos son pedidos distintos; se reconocen por `PedidoId`. | No: si lo reemplazo pierdo el pedido que el comprador está siguiendo. | BORRADOR → CONFIRMADO → ENVIADO → ENTREGADO (o CANCELADO). | [Pedido.java](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/pedido/Pedido.java) |
| **LineaPedido** | Tiene id propio; dos líneas iguales en dos pedidos distintos no son la misma. | No: el pedido modifica o quita una línea concreta. | Su cantidad y subtotal cambian mientras el pedido está en borrador. | [LineaPedido.java](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/pedido/LineaPedido.java) |
| **Publicacion** | Dos anuncios del mismo producto y precio siguen siendo publicaciones distintas (`PublicacionId`). | No: los pedidos apuntan a esa publicación en particular. | BORRADOR → PUBLICADA → AGOTADA / RETIRADA, y su stock cambia. | [Publicacion.java](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/publicacion/Publicacion.java) |

Por eso las tres tienen constructor privado, factory estático, no tienen setters y su `equals`/`hashCode` solo compara el id.

## Value Objects

| Concepto | Tipo | Por qué es value object |
|---|---|---|
| **Dinero** | record | 20.000 COP es igual a cualquier otro 20.000 COP. No cambia: `sumar` y `multiplicar` devuelven uno nuevo. |
| **Cantidad** | record | 3 unidades son 3 unidades; se valida que sea mayor a cero. |
| **Stock** | record | Solo importa el número de unidades. `descontar` devuelve un Stock nuevo en vez de modificarse. |
| **PedidoId, PublicacionId, CompradorId, VendedorId** | record | Envuelven un UUID; dos ids con el mismo UUID son el mismo valor. |
| **EspecieDestino** | enum | PERRO o GATO, valores fijos sin identidad propia. |
| **EstadoPedido** | enum | Estado del pedido; se reemplaza, no se modifica. |
| **EstadoPublicacion** | enum | Estado de la publicación. |
| **CategoriaProducto** | enum | Clasificación fija del producto. |

## Casos que se prestan a duda

- **Stock** podría parecer entidad porque "cambia", pero lo que cambia es la publicación: la publicación reemplaza su Stock por otro nuevo. El Stock en sí no tiene identidad.
- **Comprador y Vendedor** sí son entidades en el negocio, pero en esta entrega no los modelamos como agregados; el dominio solo guarda su id.
