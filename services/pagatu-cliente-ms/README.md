# pagatu-cliente-ms

Microservicio de **cliente**: guarda el perfil del cliente (persona natural o jurídica) de `pagatu`.

## Responsabilidad

Es la única fuente de verdad del perfil del cliente (DNI/RUC, nombre o razón social, contacto). Separado de `auth-ms` (S7) a propósito: `auth-ms` sabe *quién puede entrar*, `cliente-ms` sabe *quién es esa persona* para el negocio.

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

La aplicación queda en `http://localhost:8084` (Swagger: `http://localhost:8084/swagger-ui.html`) — puerto fijo, distinto de `pagatu-catalogo-ms` y `pagatu-orden-ms`. PostgreSQL DEV en `localhost:15433` (`pagatu_cliente_db`).

## Variables de entorno

Ver `.env.example`. Solo se usan al ejecutar en producción local con Docker; en DEV los valores están fijos en `application-dev.yml`.

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/v1/clientes` | Listar clientes |
| GET | `/api/v1/clientes/{id}` | Obtener un cliente |
| POST | `/api/v1/clientes` | Registrar un cliente |
| PUT | `/api/v1/clientes/{id}` | Actualizar un cliente |
| DELETE | `/api/v1/clientes/{id}` | Eliminar un cliente |

Documentación interactiva (Swagger): `http://localhost:8084/swagger-ui.html`. Health: `http://localhost:8084/actuator/health`.

### Persona natural vs. jurídica

`tipoPersona` es `NATURAL` o `JURIDICA`. Una persona natural puede tener **solo DNI**, o **DNI y RUC a la vez** (negocio unipersonal); una persona jurídica solo tiene RUC. `dni` y `ruc` son columnas independientes, ambas opcionales y únicas.

```json
POST /api/v1/clientes
{
  "tipoPersona": "NATURAL",
  "dni": "87654321",
  "nombreCompleto": "Maria Torres Quispe",
  "direccion": "Av. Los Olivos 123, Lima",
  "email": "maria.torres@example.com",
  "whatsapp": "999888777"
}
```

## Base de datos

Migraciones Flyway en `src/main/resources/db/migration/`:

- `V1__create_cliente_tables.sql` — tabla `clientes`.
- `V2__seed_clientes.sql` — datos de prueba: 4 clientes (2 `NATURAL`, 2 `JURIDICA`); uno de los `NATURAL` tiene DNI **y** RUC (negocio unipersonal).

`ddl-auto: validate` — Flyway crea/versiona el esquema, Hibernate solo valida que las entidades coincidan.

## Producción local con Docker

```powershell
docker compose up -d --build
```

## Próximos cambios (no implementados todavía)

- **S2 (guiado)**: migración a configuración centralizada (Config Server).
- **Autocompletado RENIEC/SUNAT**: hoy el CRUD guarda lo que le envíen; falta la integración real que autocomplete `nombreCompleto`/`razonSocial` a partir de `dni`/`ruc`.
- **S7**: consultado por `orden-ms` para validar `dni`/`ruc` según el `tipoComprobante` elegido; protegido con los mismos roles de `auth-ms`.
