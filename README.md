# pagatu

Sistema distribuido de comercio electrónico — proyecto del curso **Desarrollo de Aplicaciones Distribuidas** (DIST), UPeU 2026-2.

## Documentación

La documentación completa del curso (sílabo, guías de sesión paso a paso, arquitectura del sistema) vive en [`docs/`](docs/index.md) y se publica como sitio con MkDocs:

**[262dist.github.io/pagatu](https://262dist.github.io/pagatu/)**

## Estructura del repositorio

```text
pagatu/
├── docs/                      # Documentación del curso (MkDocs)
│   ├── sesiones/               # Guías paso a paso (S01, S02, ...)
│   └── silabo_dist_2026_2.md   # Sílabo vigente
├── infra/                     # Infraestructura que los microservicios necesitan para funcionar
│   ├── pagatu-config/          # Config Server (S2)
│   ├── pagatu-eureka/          # Registro y descubrimiento de servicios (S3)
│   └── pagatu-gateway/         # Punto único de acceso y balanceo de carga (S4)
├── services/                  # Microservicios de negocio
│   ├── pagatu-catalogo-ms/     # Categorías y productos (S1)
│   ├── pagatu-orden-ms/        # Órdenes de compra (S2 base autónoma, S6 Feign + Circuit Breaker)
│   ├── pagatu-cliente-ms/      # Perfil de cliente + RENIEC/SUNAT (S2, trabajo autónomo)
│   ├── pagatu-auth-ms/         # Autenticación y emisión de JWT (S7, temporal: luego Keycloak)
│   └── pagatu-pago-ms/         # Pagos; consume orden.creada y publica pago.validado (S8)
├── obs/                       # Prometheus + Loki + Promtail + Grafana (S3-S4) — observa infra/ y services/ desde afuera, no es una dependencia de arranque
├── kafka/                     # Kafka y Kafka UI (S8)
└── clients/                   # (S11, pendiente) Frontend Angular
```

## Requisitos

- JDK 21
- Docker Desktop (en DEV solo para PostgreSQL y la observabilidad)
- Git
- No hace falta instalar Maven: cada proyecto trae Maven Wrapper (`mvnw`/`mvnw.cmd`)

## Arranque rápido en DEV

El **orden importa**: primero la infraestructura, después los servicios. Cada aplicación corre en su propia terminal, parada en la carpeta indicada. Los comandos son de PowerShell; en macOS/Linux, `.\mvnw.cmd` es `./mvnw`.

**1. Infraestructura** (detalle en [`infra/README.md`](infra/README.md))

```powershell
cd infra/pagatu-config
.\mvnw.cmd spring-boot:run
```

```powershell
cd infra/pagatu-eureka
.\mvnw.cmd spring-boot:run
```

```powershell
cd infra/pagatu-gateway
.\mvnw.cmd spring-boot:run
```

**2. Kafka** (detalle en [`kafka/README.md`](kafka/README.md)) — en DEV, `pagatu-orden-ms` y `pagatu-pago-ms` corren en el host y se conectan por el puerto externo (`localhost:19092`), así que técnicamente tolerarían arrancar antes que Kafka. En PROD local no: ahí corren como contenedores en la misma red Docker y se conectan por el puerto interno (`pagatu-kafka:9092`) — si Kafka no está arriba, el contenedor del servicio ni siquiera arranca. Se levanta primero en los dos ambientes, para que el orden sea siempre el mismo:

```powershell
cd kafka
docker compose -f compose-dev.yml up -d
```

Verifica en `http://localhost:18085` (Kafka UI) que el clúster `pagatu-dev` aparece conectado.

**3. Servicios** (detalle en [`services/README.md`](services/README.md)) — la base de datos primero, después la aplicación:

```powershell
cd services/pagatu-catalogo-ms
docker compose -f compose-dev.yml up -d
.\mvnw.cmd spring-boot:run
```

```powershell
cd services/pagatu-orden-ms
docker compose -f compose-dev.yml up -d
.\mvnw.cmd spring-boot:run
```

El resto de servicios (`pagatu-cliente-ms`, `pagatu-auth-ms`, `pagatu-pago-ms`) siguen el mismo patrón, cada uno en su carpeta de `services/`.

**4. Verificar**

- Dashboard de Eureka: `http://localhost:18761`. Deben aparecer los servicios registrados.
- Una llamada a través del Gateway:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:18080/api/v1/categorias"
```

**5. Observabilidad (opcional)** (detalle en [`obs/README.md`](obs/README.md))

```powershell
cd obs
docker compose -f compose-dev.yml up -d
```

## Puertos en DEV

| Componente | Puerto (host) |
|---|---|
| `pagatu-config` | `18888` |
| `pagatu-eureka` | `18761` |
| `pagatu-gateway` | `18080` |
| `pagatu-catalogo-ms` | `8080` (2.ª instancia: `8081`) |
| `pagatu-orden-ms` | `8082` (2.ª instancia: `8083`) |
| `pagatu-cliente-ms` | `8084` |
| `pagatu-auth-ms` | `8085` |
| `pagatu-pago-ms` | `8086` |
| PostgreSQL: auth / catálogo / cliente / orden / pago | `15431` / `15432` / `15433` / `15434` / `15435` |
| Prometheus / Loki / Grafana | `19090` / `13100` / `13000` |
| Kafka broker / Kafka UI (S8) | `19092` / `18085` (PROD local: `29092` / `28085`) |
| Angular (S11) | `4200` |

En producción local, los puertos de la infraestructura y de las bases de datos usan el rango `2xxxx` (por ejemplo, `pagatu-config` en `28888`).

## Arquitectura

Diagramas C4 (contexto y contenedores) en [`docs/index.md`](docs/index.md#arquitectura-pagatu-v2026).

## Tecnologías

- Java 21, Spring Boot 4.x
- Spring Cloud: Config, Eureka y Gateway; Feign y Resilience4j (S6)
- Spring Security con JWT (S7)
- PostgreSQL, Flyway (migraciones versionadas), MapStruct
- Kafka (S8)
- Prometheus, Loki y Grafana
- Docker / Docker Compose
- Angular 22 (S11)
