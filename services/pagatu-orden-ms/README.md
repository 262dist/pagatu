# pagatu-orden-ms

Microservicio de **órdenes**: registra las compras de un cliente sobre productos de `pagatu-catalogo-ms`.

## Responsabilidad

Crea y consulta órdenes de compra, con sus ítems, comprobante, método y momento de pago. No guarda catálogo ni datos de cliente — solo referencia sus `id` (`id_producto`, `id_cliente`), sin llave foránea entre bases de datos.

## Tecnologías

Java 21 · Spring Boot 4.0.7 · Spring Data JPA · PostgreSQL · Flyway · MapStruct · SpringDoc OpenAPI · Spring Boot Actuator

## Requisitos

- JDK 21
- Docker Desktop (para PostgreSQL en DEV)
- No hace falta instalar Maven: el proyecto trae Maven Wrapper (`mvnw`/`mvnw.cmd`)

## Ejecutar en DEV

```powershell
docker compose -f compose-dev.yml up -d
.\mvnw.cmd spring-boot:run
```

La aplicación queda en `http://localhost:8082` (puerto fijo, distinto del `8080` de `pagatu-catalogo-ms`, para poder correr ambos a la vez). PostgreSQL DEV en `localhost:15434` (`pagatu_orden_db`).

## Variables de entorno

Ver `.env.example`. Solo se usan al ejecutar en producción local con Docker; en DEV los valores están fijos en `application-dev.yml`.

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/ordenes` | Listar órdenes (con sus ítems) |
| GET | `/api/v1/ordenes/{id}` | Obtener una orden |
| POST | `/api/v1/ordenes` | Crear una orden con su lista completa de ítems |
| PATCH | `/api/v1/ordenes/{id}/estado` | Cambiar el estado de una orden |

Documentación interactiva (Swagger): `http://localhost:8082/swagger-ui.html`. Health: `http://localhost:8082/actuator/health`.

### Ejemplo de creación de orden

```json
POST /api/v1/ordenes
{
  "idCliente": 1,
  "metodoPago": "YAPE_PLIN",
  "momentoPago": "ADELANTADO",
  "tipoComprobante": "BOLETA_SIMPLE",
  "items": [
    { "idProducto": 1, "cantidad": 2, "precioUnitario": 89.90 }
  ]
}
```

`tipoComprobante` y `momentoPago` son opcionales (por defecto `BOLETA_SIMPLE` y `ADELANTADO`). `metodoPago` es obligatorio: `TARJETA`, `YAPE_PLIN`, `TRANSFERENCIA` o `PAGO_EFECTIVO`.

## Base de datos

Migraciones Flyway en `src/main/resources/db/migration/`:

- `V1__create_orden_tables.sql` — tablas `ordenes` y `orden_detalles`.

Sin `V2` de datos semilla: las órdenes se crean referenciando clientes de `pagatu-cliente-ms` (`V2__seed_clientes.sql`) y productos de `pagatu-catalogo-ms` (`V2__seed_categorias_productos.sql`).

`ddl-auto: validate` — Flyway crea/versiona el esquema, Hibernate solo valida que las entidades coincidan.

## Producción local con Docker

```powershell
docker compose up -d --build
```

## Próximos cambios (no implementados todavía)

- **S2 (guiado)**: migración a configuración centralizada (Config Server).
- **S6**: `orden-ms` valida producto y precio en `pagatu-catalogo-ms` vía Feign + Circuit Breaker, antes de crear la orden.
- **S7**: `id_cliente` deja de venir en el request y se toma del JWT ya validado.
- **S8**: al crear una orden, se publica el evento `orden.creada` para `pago-ms`.
- **S9**: idempotencia al crear (evitar duplicados) y compensación (revertir si el pago falla).
