# infra

Componentes de infraestructura compartida de `pagatu`: hoy, el servidor de configuración centralizada (`pagatu-config`). Eureka (S3) y el Gateway (S4) se agregan aquí mismo, como proyectos hermanos, cuando esas sesiones los construyan.

## Responsabilidad

A diferencia de `services/`, ningún proyecto de `infra/` implementa lógica de negocio — son piezas que los microservicios de negocio (`pagatu-catalogo-ms`, `orden-ms`, `cliente-ms`) consultan para funcionar como sistema distribuido: configuración externa, registro/descubrimiento de servicios, punto único de entrada.

## Componentes

| Carpeta | Qué es | Desde qué sesión |
|---|---|---|
| `pagatu-config/` | Config Server (Spring Cloud Config) — entrega la configuración por ambiente (`dev`/`prod`) de cada microservicio por HTTP, leyendo `config-repo/`. | S2 |
| `eureka-server/` (previsto) | Registro y descubrimiento de servicios. | S3 |
| `gateway/` (previsto) | Punto único de acceso y balanceo de carga. | S4 |

## Tecnologías

Java 21 · Spring Boot 4.0.7 · Spring Cloud 2025.1.2 (*Oakwood*) · Spring Boot Actuator

## Requisitos

- JDK 21
- Docker Desktop (solo para producción local — en DEV, `pagatu-config` corre con Maven Wrapper en el host, sin Docker)
- No hace falta instalar Maven: cada proyecto trae Maven Wrapper (`mvnw`/`mvnw.cmd`)

## Ejecutar `pagatu-config` en DEV

```powershell
cd infra/pagatu-config
.\mvnw.cmd spring-boot:run
```

Queda en `http://localhost:18888`, leyendo `config-repo/` (carpeta local dentro del propio proyecto — no un repositorio Git remoto). Debe ejecutarse parado exactamente en `infra/pagatu-config`; el path de `config-repo` es relativo (`file:./config-repo`).

Verifica que está activo:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:18888/actuator/health"
```

## Consultar la configuración de un microservicio

Convención de nombres en `config-repo/`: `{spring.application.name}-{perfil}.yml`.

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:18888/pagatu-catalogo-ms/dev"
Invoke-RestMethod -Method Get -Uri "http://localhost:18888/pagatu-catalogo-ms/prod"
```

Respuesta `200 OK` con `propertySources` vacío significa que el archivo `{app}-{perfil}.yml` no existe todavía en `config-repo/`, o que el nombre no coincide letra por letra con `spring.application.name` del microservicio.

## Archivos de configuración (`config-repo/`)

- `pagatu-catalogo-ms-dev.yml` / `pagatu-catalogo-ms-prod.yml`
- `orden-ms-dev.yml` / `orden-ms-prod.yml` (previsto, trabajo autónomo de S2)

`config-repo` es una carpeta local versionada en este mismo repositorio (no un Git remoto separado, a diferencia del backend `git:` nativo de Spring Cloud Config) — como `pagatu` es público en GitHub, **no se comitean credenciales reales aquí**: los valores actuales (`pagatu`/`pagatu`) son de prueba, para DEV/PROD local únicamente.

## Producción local con Docker

```powershell
cd infra
docker compose up -d --build
```

`pagatu-config` queda expuesto en `http://localhost:28888` (host) / `http://pagatu-config:8888` (desde otros contenedores en la red `pagatu-prod-net`, que este `compose.yml` crea). Cualquier microservicio en `services/` que necesite resolverlo por nombre debe unirse a esa misma red como `external: true` — el orden de arranque importa: primero `infra`, después `services/*`.

Detalle completo (Dockerfile, red compartida, verificación paso a paso) en la guía de sesión: [`docs/sesiones/S02_Configuracion_Centralizada_Ambientes.md`](../docs/sesiones/S02_Configuracion_Centralizada_Ambientes.md).

## Puertos

| Componente | Puerto DEV (host) | Puerto PROD local (host) |
|---|---|---|
| `pagatu-config` | `18888` | `28888` |

## Próximos cambios (no implementados todavía)

- **S3**: `eureka-server` — registro y descubrimiento de servicios; `pagatu-catalogo-ms` y `orden-ms` se registran ahí en vez de consultarse por URL fija.
- **S4**: `gateway` — punto único de acceso y balanceo de carga sobre los servicios ya registrados en Eureka.
