# pagatu-catalogo-ms

Microservicio de **catálogo**: gestiona categorías y productos de `pagatu`.

## Responsabilidad

Es la única fuente de verdad para categorías y productos — ningún otro microservicio guarda estos datos, los consultan por HTTP (a partir de S6, `orden-ms` lo hace por Feign).

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

La aplicación queda en `http://localhost:8080` (Swagger: `http://localhost:8080/swagger-ui.html`). PostgreSQL DEV en `localhost:15432` (`pagatu_catalogo_db`).

## Variables de entorno

Ver `.env.example`. Solo se usan al ejecutar en producción local con Docker (`compose.yml`); en DEV los valores están fijos en `application-dev.yml`.

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/categorias` | Listar categorías |
| GET | `/api/v1/categorias/{id}` | Obtener una categoría |
| POST | `/api/v1/categorias` | Crear categoría |
| PUT | `/api/v1/categorias/{id}` | Actualizar categoría |
| DELETE | `/api/v1/categorias/{id}` | Eliminar categoría |
| GET | `/api/v1/productos` | Listar productos (con su categoría) |
| GET | `/api/v1/productos/{id}` | Obtener un producto |
| POST | `/api/v1/productos` | Crear producto |
| PUT | `/api/v1/productos/{id}` | Actualizar producto |
| DELETE | `/api/v1/productos/{id}` | Eliminar producto |

Documentación interactiva (Swagger): `http://localhost:8080/swagger-ui.html`. Health: `http://localhost:8080/actuator/health`.

## Base de datos

Migraciones Flyway en `src/main/resources/db/migration/`:

- `V1__create_catalogo_tables.sql` — tablas `categorias` y `productos`.
- `V2__seed_categorias_productos.sql` — datos de prueba: 4 categorías, 7 productos.

`ddl-auto: validate` — Flyway crea/versiona el esquema, Hibernate solo valida que las entidades coincidan.

## Producción local con Docker

```powershell
docker compose up -d --build
```

Detalle completo (Dockerfile, red compartida, escalamiento) en la guía de sesión: [`docs/sesiones/S01_Construccion_Servicio_Base.md`](../../docs/sesiones/S01_Construccion_Servicio_Base.md).

## Próximos cambios (no implementados todavía)

- **S2**: migración a configuración centralizada (Config Server) — `application-dev.yml`/`application-prod.yml` locales se moverán a `config-repo`.
- **S6/S9**: `productos.stock` ya existe en el esquema y en la API, pero todavía no hay ninguna operación que lo descuente de forma segura (`UPDATE ... WHERE stock >= cantidad`, idempotencia, compensación) — eso se construye cuando `orden-ms` empiece a llamar a este servicio.
