# pagatu

Sistema distribuido de comercio electrónico — proyecto del curso **Desarrollo de Aplicaciones Distribuidas** (DIST), UPeU 2026-2.

## Documentación

La documentación completa del curso (sílabo, guías de sesión paso a paso, arquitectura del sistema) vive en [`docs/`](docs/index.md) y se publica como sitio con MkDocs:

**[262dist.github.io/pagatu](https://262dist.github.io/pagatu/)**

## Estructura del repositorio

```text
pagatu/
├── docs/                    # Documentación del curso (MkDocs)
│   ├── sesiones/             # Guías paso a paso (S01, S02, ...)
│   └── silabo_dist_2026_2.md # Sílabo vigente
├── services/                 # Microservicios
│   ├── pagatu-catalogo-ms/   # Categorías y productos (S1)
│   ├── pagatu-orden-ms/      # Órdenes de compra (S2)
│   └── pagatu-cliente-ms/    # Perfil de cliente + RENIEC/SUNAT (S2)
├── infra/                    # Config Server, Eureka, Gateway (pendiente de crear)
└── clients/                  # Frontend Angular (pendiente, desde S11)
```

## Arquitectura

Diagramas C4 (contexto y contenedores) en [`docs/index.md`](docs/index.md#arquitectura-pagatu-v2026).

## Cómo ejecutar un microservicio en DEV

Cada microservicio en `services/` sigue el mismo patrón: PostgreSQL en Docker, la aplicación con Maven Wrapper desde el host.

```powershell
cd services/pagatu-catalogo-ms
docker compose -f compose-dev.yml up -d
.\mvnw.cmd spring-boot:run
```

El detalle completo (puertos, variables de entorno, producción local con Docker) está en la guía de sesión correspondiente dentro de `docs/sesiones/`.

## Tecnologías

- Java 21, Spring Boot 4.0.7
- PostgreSQL, Flyway (migraciones versionadas)
- MapStruct (mapeo DTO ↔ entidad)
- Docker / Docker Compose
- Spring Cloud Config (S2), Eureka (S3), Gateway (S4)
- Angular 21 (S11)
