# EcommerceMascotas

Marketplace de productos para **perros y gatos** con dos roles: comprador y vendedor.
Proyecto del curso Programación Avanzada, Universidad del Quindío, 2026-2.

**Entrega 1:** modelo de dominio con DDD y arquitectura por capas.

## Stack

- Java 21
- Spring Boot 4.1.1 (Gradle)
- JUnit 5

## Estructura

```
ecommerce/src/main/java/com/uniquindio/ecommerce
├── domain
│   ├── entity        → Pedido, LineaPedido, Publicacion
│   ├── valueobject   → Dinero, Cantidad, Stock, EspecieDestino, PedidoId, PublicacionId,
│   │                   CompradorId, VendedorId, EstadoPedido, EstadoPublicacion, CategoriaProducto
│   ├── repository    → PedidoRepository, PublicacionRepository (interfaces), Pagina, PublicacionVentas
│   └── exception     → ReglaDominioException
├── application
│   ├── dto
│   │   ├── request   → CrearPublicacionRequest, CrearPedidoRequest
│   │   └── response  → PedidoResponse, PublicacionResponse, PageResponse, MasVendidaResponse
│   └── usecase       → vendedor: CrearPublicacionUseCase, ActualizarStockPublicacionUseCase, RetirarPublicacionUseCase
│                       comprador: CrearPedidoUseCase, ConfirmarPedidoUseCase, CancelarPedidoUseCase
│                       consultas: ConsultarCatalogoUseCase, ConsultarPedidosUseCase
└── infrastructure
    ├── config        → OpenApiConfig (Swagger)
    ├── persistence   → PedidoRepositoryEnMemoria, PublicacionRepositoryEnMemoria (solo para pruebas)
    │   └── jpa       → PedidoRepositoryJpa, PublicacionRepositoryJpa (+ sus repositorios de Spring Data)
    │       ├── entity → PedidoEntity, LineaPedidoEntity, PublicacionEntity, DineroEmbeddable
    │       └── mapper → PedidoPersistenceMapper, PublicacionPersistenceMapper
    └── rest          → ConsultasPublicacionController, PedidoController, ConsultasPedidoController
        ├── mapper    → PedidoMapper (DTO ↔ dominio)
        └── exception → GlobalExceptionHandler
```

Las clases se organizan **por tipo** (entidades, value objects, repositorios, excepción, casos de uso, DTOs), siguiendo el repositorio guía del curso.
El paquete `domain` no depende de Spring, JPA ni Lombok. Los repositorios son interfaces en el dominio; la aplicación usa su implementación con JPA (`…RepositoryJpa`) y las versiones en memoria quedan para pruebas.

## Persistencia

- Base de datos H2 en memoria; las tablas se crean al arrancar: `publicaciones`, `pedidos` y `lineas_pedido`.
- Las entidades JPA (`infrastructure/persistence/jpa/entity`) son clases distintas de las entidades del dominio; un mapper convierte entre ellas usando `reconstruir(...)` del dominio.
- `data.sql` carga datos de prueba: 10 publicaciones de perro y gato (una AGOTADA y una RETIRADA) y 7 pedidos en distintos estados.
- Retirar una publicación no borra la fila: queda en estado RETIRADA (eliminación lógica).

## Consultas

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/api/publicaciones?texto=&especie=&categoria=&pagina=0&tamano=10` | Catálogo paginado: busca el texto en el nombre o la descripción y filtra por especie (`PERRO`, `GATO`) y categoría (`ALIMENTO`, `JUGUETE`, `ACCESORIO`, `HIGIENE`, `SALUD`). No muestra las publicaciones retiradas. Responde el contenido, `totalElementos` y `totalPaginas` |
| GET | `/api/publicaciones/mas-vendidas?limite=5` | Ranking de las publicaciones con más unidades vendidas (pedidos confirmados, enviados o entregados) |
| GET | `/api/publicaciones/{id}` | Detalle de una publicación (404 si no existe) |

- El catálogo es una consulta JPQL con parámetros con nombre, y el ranking usa `JOIN`, `GROUP BY` y `SUM`: el filtro, el conteo y la paginación los hace la base de datos, no Java.
- El dominio no usa la paginación de Spring: el adaptador convierte el `Page` de Spring en `Pagina`, que es del dominio.
- La página no puede ser negativa y el tamaño va de 1 a 50; si no, responde 400.

## Pedidos

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/pedidos` | Crea un pedido en BORRADOR con sus líneas. Responde 201 con la dirección del pedido nuevo |
| PUT | `/api/pedidos/{id}/confirmar` | Confirma el pedido y descuenta el stock de cada publicación |
| PUT | `/api/pedidos/{id}/cancelar` | Cancela el pedido; si ya estaba confirmado, el stock vuelve a las publicaciones |
| GET | `/api/pedidos?compradorId=` | Pedidos de un comprador |
| GET | `/api/pedidos/{id}` | Detalle de un pedido (404 si no existe) |

Ejemplo para crear un pedido con los datos de prueba (2 rascadores para el comprador `c1c1…0002`):

```json
{
  "compradorId": "c1c1c1c1-0000-4000-8000-000000000002",
  "lineas": [{ "publicacionId": "b0b0b0b0-0000-4000-8000-000000000005", "cantidad": 2 }]
}
```

- El precio no viaja en la petición: lo toma el dominio de la publicación, así nadie puede mandar un precio inventado.
- Los casos de uso reciben tipos del dominio (`CompradorId`, `PedidoId`, `PublicacionId`, `Cantidad`) y devuelven el `Pedido`. `PedidoMapper` convierte el DTO en esos tipos y el `Pedido` en `PedidoResponse`, así el caso de uso no depende de HTTP.
- Errores de negocio a propósito para probar (409): pedir más unidades que el stock, pedir una publicación retirada (`…0009`), confirmar dos veces el mismo pedido o cancelar un pedido ENVIADO (`d0d0…0003`).

## Correcciones de la Entrega 1

| Observación que nos dieron | Qué cambiamos | Dónde se ve (archivo o rama) |
|---|---|---|
| Las clases del dominio estaban mezcladas por agregado (entidades, enums, value objects e interfaces en la misma carpeta) | Las reorganizamos por tipo: `domain/entity`, `domain/valueobject`, `domain/repository` y `domain/exception`; los casos de uso pasaron a `application/usecase` con el sufijo `UseCase`, y los DTOs a `dto/request` y `dto/response`. La lógica no cambió y las pruebas siguen en verde | `ecommerce/src/main/java/com/uniquindio/ecommerce/domain/`, rama `fix/estructura-por-tipo` |
| La publicación tenía que tener una descripción, sí o sí | `Publicacion` tiene una `descripcion` obligatoria, protegida como invariante en `crear(...)`: no puede ser vacía ni superar 500 caracteres. Se agregó al DTO `CrearPublicacionRequest`, a la documentación y una prueba que lo demuestra | `domain/entity/Publicacion.java`, `PublicacionTest.crearPublicacionSinDescripcion…`, rama `fix/descripcion-publicacion` |

### Otras mejoras que encontramos al revisar el código

| Problema | Qué cambiamos | Dónde se ve |
|---|---|---|
| `confirmar()` solo revisaba el stock: si el vendedor retiraba una publicación mientras el pedido estaba en borrador, el error salía después, al descontar el stock | `Pedido.confirmar()` ahora revisa que cada publicación siga aceptando pedidos antes de pasar a CONFIRMADO; nueva prueba que lo demuestra | `domain/entity/Pedido.java`, `PedidoTest.confirmarConUnaPublicacionRetirada…` |
| Cuando un pedido o una publicación no existía, el caso de uso lanzaba `ReglaDominioException`, igual que si se rompiera una regla | Ahora lanza `NoSuchElementException` (se podrá responder 404), y `ReglaDominioException` queda solo para reglas de negocio rotas | `application/usecase/CrearPedidoUseCase.java`, `ConfirmarPedidoUseCase.java` |
| `diagrama-agregado.md` hablaba de "PedidoMascotas" y "productos", términos que no están en el lenguaje ubicuo | Se usa "Pedido" y "línea de pedido", igual que en el código | `diagrama-agregado.md` |
| Los casos de uso de pedidos recibían un DTO (`CrearPedidoRequest`) o devolvían uno (`PedidoResponse`), y eso dejaba la capa de aplicación amarrada a la API | Ahora reciben tipos del dominio y devuelven el `Pedido`; el `PedidoMapper` de la API hace la conversión en los dos sentidos | `application/usecase/CrearPedidoUseCase.java`, `infrastructure/rest/mapper/PedidoMapper.java` |

## Documentación

- [Lenguaje ubicuo](lenguaje-ubicuo.md)
- [Clasificación entidad / value object](clasificacion-entidades-vo.md)
- [Reto de diseño y reglas de negocio](reto-diseno.md)
- [Agregados, invariantes y diagrama](diagrama-agregado.md)
- [Casos de uso](casos-de-uso.md)
- [DTOs](dtos.md)

## Cómo ejecutar el proyecto

Requisitos: Java 21. Desde la carpeta `ecommerce`:

```bash
./gradlew bootRun
```

En Windows: `gradlew.bat bootRun`. Con la aplicación corriendo:

| Qué | Dirección |
|---|---|
| Swagger (probar la API) | http://localhost:8080/swagger-ui.html |
| Documentación OpenAPI en JSON | http://localhost:8080/api-docs |
| Consola de la base de datos H2 | http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:ecommercedb`, usuario `sa`, sin contraseña) |

Errores de la API: 400 datos inválidos, 404 el recurso no existe, 409 se rompió una regla de negocio. Todos responden un JSON con `status`, `error` y `mensaje`.

## Cómo correr las pruebas

Desde la carpeta `ecommerce`:

```bash
./gradlew test
```

En Windows (cmd o PowerShell):

```bash
gradlew.bat test
```
