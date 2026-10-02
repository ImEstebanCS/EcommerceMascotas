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
│   ├── pedido        → Pedido, LineaPedido, EstadoPedido, PedidoRepository
│   ├── publicacion   → Publicacion, EstadoPublicacion, CategoriaProducto, PublicacionRepository
│   └── shared        → Dinero, Cantidad, Stock, EspecieDestino, ids, ReglaDominioException
├── application
│   ├── pedido        → CrearPedido, ConfirmarPedido
│   ├── publicacion   → CrearPublicacion
│   └── dto           → CrearPublicacionRequest, CrearPedidoRequest, PedidoResponse
└── infrastructure
    └── persistence   → PedidoRepositoryEnMemoria, PublicacionRepositoryEnMemoria
```

El paquete `domain` no depende de Spring, JPA ni Lombok. Los repositorios son interfaces en el dominio y su implementación (con `HashMap`) está en infraestructura.

## Documentación

- [Lenguaje ubicuo](lenguaje-ubicuo.md)
- [Clasificación entidad / value object](clasificacion-entidades-vo.md)
- [Reto de diseño y reglas de negocio](reto-diseno.md)
- [Agregados, invariantes y diagrama](diagrama-agregado.md)
- [Casos de uso](casos-de-uso.md)
- [DTOs](dtos.md)

## Cómo correr las pruebas

Desde la carpeta `ecommerce`:

```bash
./gradlew test
```

En Windows (cmd o PowerShell):

```bash
gradlew.bat test
```
