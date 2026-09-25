# infra

Componentes de infraestructura compartida de `pagatu`: el servidor de configuración centralizada (`pagatu-config`, S2), el registro y descubrimiento de servicios (`pagatu-eureka`, S3) y el punto único de acceso (`pagatu-gateway`, S4).

## Responsabilidad

A diferencia de `services/`, ningún proyecto de `infra/` implementa lógica de negocio — son piezas que los microservicios de negocio (`pagatu-catalogo-ms`, `pagatu-orden-ms`, `pagatu-cliente-ms`) consultan para funcionar como sistema distribuido: configuración externa, registro/descubrimiento de servicios, punto único de entrada.

## Componentes

| Carpeta | Qué es | Desde qué sesión |
|---|---|---|
| `pagatu-config/` | Config Server (Spring Cloud Config) — entrega la configuración por ambiente (`dev`/`prod`) de cada componente por HTTP, leyendo `config-repo/`. | S2 |
| `pagatu-eureka/` | Eureka Server — registro y descubrimiento de servicios. | S3 |
| `pagatu-gateway/` | Spring Cloud Gateway (WebMVC) — punto único de acceso y balanceo de carga (`lb://`). Desde S7 valida el JWT de `pagatu-auth-ms`. | S4 |

## Tecnologías

Java 21 · Spring Boot 4.0.x · Spring Cloud 2025.1.x (*Oakwood*) · Spring Boot Actuator

## Requisitos

- JDK 21
- Docker Desktop (solo para producción local — en DEV, todo corre con Maven Wrapper en el host, sin Docker)
- No hace falta instalar Maven: cada proyecto trae Maven Wrapper (`mvnw`/`mvnw.cmd`)

## Ejecutar en DEV

El **orden importa**: `pagatu-config` primero (los otros dos leen su configuración de ahí), después `pagatu-eureka`, y por último `pagatu-gateway` (necesita Eureka para resolver las rutas `lb://`). Cada comando se ejecuta en su propia terminal, parado exactamente en la carpeta indicada.

**1. `pagatu-config`** — `http://localhost:18888`

```powershell
cd infra/pagatu-config
.\mvnw.cmd spring-boot:run
```

Lee `config-repo/` (carpeta local dentro del propio proyecto — no un repositorio Git remoto). Debe ejecutarse parado exactamente en `infra/pagatu-config`; el path de `config-repo` es relativo (`file:./config-repo`).

**2. `pagatu-eureka`** — `http://localhost:18761`

```powershell
cd infra/pagatu-eureka
.\mvnw.cmd spring-boot:run
```

El *dashboard* de Eureka queda en esa misma dirección y lista las instancias registradas.

**3. `pagatu-gateway`** — `http://localhost:18080`

```powershell
cd infra/pagatu-gateway
.\mvnw.cmd spring-boot:run
```

En macOS/Linux, el equivalente de cada comando es `./mvnw spring-boot:run`.

Verifica que los tres están activos:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:18888/actuator/health"
Invoke-RestMethod -Method Get -Uri "http://localhost:18761/actuator/health"
Invoke-RestMethod -Method Get -Uri "http://localhost:18080/actuator/health"
```

## Consultar la configuración de un componente

Convención de nombres en `config-repo/`: `{spring.application.name}-{perfil}.yml`.

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:18888/pagatu-catalogo-ms/dev"
Invoke-RestMethod -Method Get -Uri "http://localhost:18888/pagatu-gateway/dev"
```

Respuesta `200 OK` con `propertySources` vacío significa que el archivo `{app}-{perfil}.yml` no existe todavía en `config-repo/`, o que el nombre no coincide letra por letra con `spring.application.name` del componente.

## Archivos de configuración (`config-repo/`)

- `pagatu-catalogo-ms-dev.yml` / `pagatu-catalogo-ms-prod.yml`
- `pagatu-orden-ms-dev.yml` / `pagatu-orden-ms-prod.yml`
- `pagatu-eureka-dev.yml` / `pagatu-eureka-prod.yml`
- `pagatu-gateway-dev.yml` / `pagatu-gateway-prod.yml`

`config-repo` es una carpeta local versionada en este mismo repositorio (no un Git remoto separado, a diferencia del backend `git:` nativo de Spring Cloud Config) — como `pagatu` es público en GitHub, **no se comitean credenciales reales aquí**: los valores actuales (`pagatu`/`pagatu`) son de prueba, para DEV/PROD local únicamente.

## Producción local con Docker

```powershell
cd infra
docker compose up -d --build
```

`infra/compose.yml` levanta hoy `pagatu-config` y `pagatu-eureka`, en la red `pagatu-prod-net` (que este `compose.yml` crea):

- `pagatu-config`: `http://localhost:28888` (host) / `http://pagatu-config:8888` (desde otros contenedores).
- `pagatu-eureka`: `http://localhost:28761` (host) / `http://pagatu-eureka:8761` (desde otros contenedores). Arranca solo cuando `pagatu-config` está saludable.

Cualquier microservicio en `services/` que necesite resolverlos por nombre debe unirse a esa misma red como `external: true` — el orden de arranque importa: primero `infra`, después `services/*`. El servicio Docker de `pagatu-gateway` se agrega a este `compose.yml` en S4 (ver la guía de sesión).

Detalle completo en las guías de sesión: [S2 — configuración centralizada](../docs/sesiones/S02_Configuracion_Centralizada_Ambientes.md), [S3 — registro y descubrimiento](../docs/sesiones/S03_Registro_Descubrimiento_Ejecucion_Concurrente.md) y [S4 — punto único de acceso](../docs/sesiones/S04_Punto_Unico_Acceso_Distribucion_Trafico.md).

## Puertos

| Componente | Puerto DEV (host) | Puerto PROD local (host) |
|---|---|---|
| `pagatu-config` | `18888` | `28888` |
| `pagatu-eureka` | `18761` | `28761` |
| `pagatu-gateway` | `18080` | `28080` (S4) |
