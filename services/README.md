# services

Microservicios de negocio de `pagatu`. Cada carpeta es un proyecto Spring Boot independiente, con su propia base de datos PostgreSQL y su propio Maven Wrapper.

## Antes de arrancar un servicio

1. Levanta la infraestructura en DEV, en este orden: `pagatu-config`, `pagatu-eureka` y `pagatu-gateway` (ver [`infra/README.md`](../infra/README.md)). Los servicios leen su configuración de `pagatu-config` (`http://localhost:18888`) y se registran en `pagatu-eureka`.
2. Docker Desktop encendido: en DEV solo se usa para PostgreSQL. La aplicación corre con Maven Wrapper en el host.

## Servicios

| Carpeta | Qué es | Desde qué sesión | Puerto DEV (app) | Base de datos DEV |
|---|---|---|---|---|
| `pagatu-catalogo-ms/` | Categorías y productos. | S1 | `8080` (2.ª instancia: `8081`) | `pagatu_catalogo_db` en `15432` |
| `pagatu-orden-ms/` | Órdenes; llama a catálogo con Feign y Circuit Breaker. | S2 (base autónoma), S6 | `8082` (2.ª instancia: `8083`) | `pagatu_orden_db` en `15434` |
| `pagatu-cliente-ms/` | Perfil del cliente (DNI/RUC). | S2 (trabajo autónomo) | `8084` | `pagatu_cliente_db` en `15433` |
| `pagatu-auth-ms/` | Autenticación y emisión de JWT (temporal, luego Keycloak). | S7 | `8085` | `pagatu_auth_db` en `15431` |
| `pago-ms/` | Pagos; consume `orden-eventos` y publica `pago-eventos`. | S8 | por definir en S8 | `pago_db` en `15435` |

Las carpetas de `pagatu-cliente-ms`, `pagatu-auth-ms` y `pago-ms` aparecen cuando esas sesiones las construyen. Los comandos de abajo aplican a cada una en cuanto exista.

## Ejecutar en DEV

Cada comando se ejecuta parado en la carpeta del servicio. Primero la base de datos, después la aplicación.

### `pagatu-catalogo-ms`

```powershell
cd services/pagatu-catalogo-ms
docker compose -f compose-dev.yml up -d
.\mvnw.cmd spring-boot:run
```

Swagger: `http://localhost:8080/swagger-ui.html`. Health: `http://localhost:8080/actuator/health`.

Segunda instancia, en otra terminal (puerto fijo, no aleatorio):

```powershell
cd services/pagatu-catalogo-ms
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

### `pagatu-orden-ms`

```powershell
cd services/pagatu-orden-ms
docker compose -f compose-dev.yml up -d
.\mvnw.cmd spring-boot:run
```

Swagger: `http://localhost:8082/swagger-ui.html`. Health: `http://localhost:8082/actuator/health`. Segunda instancia con `"-Dspring-boot.run.arguments=--server.port=8083"`, igual que arriba.

### `pagatu-cliente-ms`

```powershell
cd services/pagatu-cliente-ms
docker compose -f compose-dev.yml up -d
.\mvnw.cmd spring-boot:run
```

Swagger: `http://localhost:8084/swagger-ui.html`. Health: `http://localhost:8084/actuator/health`.

### `pagatu-auth-ms`

```powershell
cd services/pagatu-auth-ms
docker compose -f compose-dev.yml up -d
.\mvnw.cmd spring-boot:run
```

Login: `POST http://localhost:8085/api/v1/auth/login`. Claves públicas: `http://localhost:8085/.well-known/jwks.json`. Health: `http://localhost:8085/actuator/health`.

### `pago-ms`

```powershell
cd services/pago-ms
docker compose -f compose-dev.yml up -d
.\mvnw.cmd spring-boot:run
```

El puerto de la aplicación y las dependencias de Kafka se definen en S8.

En macOS/Linux, el equivalente de cada comando es `./mvnw spring-boot:run`.

## Detener

```powershell
docker compose -f compose-dev.yml down
```

Ejecutado en la carpeta del servicio, apaga su PostgreSQL y conserva los datos (el volumen sigue existiendo). La aplicación se detiene con `Ctrl+C` en su terminal.

## Producción local

Cada servicio publica su propio `compose.yml` cuando esa sesión lo pide (hoy, solo `pagatu-catalogo-ms`). En producción local ningún microservicio expone su puerto al host: todo el tráfico entra por `pagatu-gateway`. Detalle en el `README.md` de cada servicio y en las guías de sesión.
