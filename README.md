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
│   ├── repository    → PedidoRepository, PublicacionRepository (interfaces)
│   └── exception     → ReglaDominioException
├── application
│   ├── dto
│   │   ├── request   → CrearPublicacionRequest, CrearPedidoRequest
│   │   └── response  → PedidoResponse
│   └── usecase       → vendedor: CrearPublicacionUseCase, ActualizarStockPublicacionUseCase, RetirarPublicacionUseCase
│                       comprador: CrearPedidoUseCase, ConfirmarPedidoUseCase, CancelarPedidoUseCase
└── infrastructure
    └── persistence   → PedidoRepositoryEnMemoria, PublicacionRepositoryEnMemoria
```

Las clases se organizan **por tipo** (entidades, value objects, repositorios, excepción, casos de uso, DTOs), siguiendo el repositorio guía del curso.
El paquete `domain` no depende de Spring, JPA ni Lombok. Los repositorios son interfaces en el dominio y su implementación (con `HashMap`) está en infraestructura.

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
