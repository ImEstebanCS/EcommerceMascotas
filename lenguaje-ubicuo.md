# Lenguaje ubicuo

## Nicho

Marketplace de productos para **perros y gatos**. Los **vendedores** publican productos (alimento, juguetes, accesorios, higiene y salud) y los **compradores** hacen pedidos sobre esas publicaciones.

## Términos

| Término | Significado en el nicho | Dónde vive en el código |
|---|---|---|
| **Publicación** | Anuncio de un producto que un vendedor pone a la venta, con precio, stock y especie destino. | [Publicacion.java](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/publicacion/Publicacion.java) |
| **Especie destino** | Especie a la que está dirigido el producto: PERRO o GATO. Es la regla dura del nicho. | [EspecieDestino.java](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/shared/EspecieDestino.java) |
| **Pedido** | Solicitud de compra de una o más publicaciones hecha por un comprador. | [Pedido.java](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/pedido/Pedido.java) |
| **Línea de pedido** | Cada ítem dentro de un pedido: publicación + cantidad + subtotal. | [LineaPedido.java](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/pedido/LineaPedido.java) |
| **Stock** | Unidades disponibles de una publicación. | [Stock.java](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/shared/Stock.java) |
| **Agotada** | Estado de una publicación cuando su stock llega a 0. Ya no recibe pedidos hasta que el vendedor reponga stock. | [EstadoPublicacion.java](ecommerce/src/main/java/com/uniquindio/ecommerce/domain/publicacion/EstadoPublicacion.java) |
