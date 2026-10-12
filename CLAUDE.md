# DIST - pagatu

Workspace del curso **Desarrollo de Aplicaciones Distribuidas** (DIST),
UPeU 2026-2. Contiene tanto la documentación (MkDocs) como el código real de
los microservicios del proyecto `pagatu` — esta nota estaba desactualizada:
decía que el repo era solo de documentación, sin código ni sesiones; ya no
es así desde hace varias sesiones.

## Dónde está cada cosa

- `docs/silabo_dist_2026_2.md` — sílabo oficial **vigente** (no editar salvo
  pedido explícito). `docs/silabo_dist_2026_1.md` es la versión anterior,
  solo como referencia histórica.
- `docs/index.md` — página de bienvenida curada (propósito del curso,
  producto del curso).
- `docs/sesiones/S0X_*.md` — guías de sesión, publicadas en `mkdocs.yml`.
  **Estado real (2026-10-10):** S01-S10 completamente desarrolladas, con
  código real enlazado y verificado contra los servicios reales de
  `services/`; S12 y S15 son evaluaciones de unidad. S11 todavía no existe.
- `docs/propuesta-proyecto/` — **obsoleta**, el usuario la va a borrar; la
  convención real de arquitectura ya está en los `.md` de `services/*/`
  (ver `services/README.md`) y en las propias guías de sesión.
- `infra/` — `pagatu-config` (Config Server, `config-repo/` con el
  `application-*.yml` real de cada servicio), `pagatu-eureka`,
  `pagatu-gateway`.
- `services/` — microservicios reales: `pagatu-auth-ms`, `pagatu-catalogo-ms`,
  `pagatu-orden-ms`, `pagatu-pago-ms` (Spring Boot 4.0.7/4.1.1, Java 21).
  Cada uno con Postgres propio, Flyway, `logback-spring.xml` con
  `CorrelationIdFilter` (`X-Trace-ID`/MDC `traceId`), Actuator + Micrometer
  (`/actuator/prometheus`).
- `obs/` — Prometheus, Loki, Promtail, Grafana (S3-S4; log centralizado
  de los cuatro servicios y tablero de diagnóstico desde S10). Prometheus
  descubre los microservicios solo, por Eureka; Promtail necesita un
  `job_name` + volumen explícito por servicio en `compose-dev.yml`/
  `compose.yml` (sin *service discovery* para logs).
- `mkdocs.yml` — nav con sílabos y las sesiones por unidad.

## Convenciones

- No editar `docs/silabo_dist_*.md` salvo pedido explícito — son contenido
  pedagógico publicado.
- Al avanzar una sesión nueva, seguir el patrón ya establecido en S01-S10:
  verificar el estado real de `services/`/`infra/`/`obs/` antes de asumir
  qué existe (varias sesiones encontraron brechas reales entre lo
  documentado y lo implementado — no dar por hecho el estado sin comprobarlo).
- `git`: cada sesión cerrada tiene su propia rama (`sXX-nombre-tema`,
  `git branch` + `git push -u origin`, sin `checkout -b` — el usuario se
  queda en `main`).
