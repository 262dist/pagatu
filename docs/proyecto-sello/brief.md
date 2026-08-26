# Brief Técnico del Proyecto Sello

Este documento es el hito de **S2** del cronograma (ver [Guía del Proyecto Sello](index.md), sección 4): el punto donde el equipo declara, por escrito, qué sistema distribuido va a construir durante el ciclo — antes de avanzar a S3 (registro y descubrimiento). No reemplaza el informe final; es la ficha corta que fija el rumbo desde el principio.

`pagatu` (las guías de S1 en adelante) es el sistema de referencia del curso — muestra **cómo** se construye cada capacidad distribuida (Config Server, Eureka, Gateway, seguridad, mensajería, observabilidad). Tu equipo aplica ese mismo patrón sobre **su propio dominio de negocio**, no necesariamente sobre comercio electrónico ni con los mismos nombres de microservicio (`catalogo-ms`, `orden-ms`, etc.) — el repositorio se identifica con su propio `grupo-<numero>-<nombre-proyecto>` en los topics (ver [Guía del Proyecto Sello](index.md), sección 5).

Cada equipo llena una sola copia de este brief, la publica en su repositorio (o en su MkDocs) y la actualiza solo si el alcance cambia de verdad — no en cada sesión.

## 1. Datos del equipo

- Nombre del equipo:
- Nombre del proyecto (coincide con `grupo-<numero>-<nombre-proyecto>` en los topics):
- Sección:
- Repositorio (URL):
- Topics del repositorio configurados (sí/no):

**Integrantes:**

| Integrante | Rol o énfasis previsto (ej. seguridad, mensajería, frontend) |
|---|---|
| | |
| | |
| | |
| | |

## 2. Dominio del proyecto

- Problema o necesidad que resuelve (2-4 líneas):
- Flujo de negocio de extremo a extremo (breve — el proceso que atraviesa varios microservicios, equivalente a "catálogo → orden → pago" en `pagatu`):
- Usuarios / actores principales (roles que interactúan con el sistema):
- Sistemas externos con los que interactúa, si los hay (ej. pasarela de pagos, un servicio de terceros — equivalente a RENIEC/SUNAT en `pagatu`):
- ¿Continúa un proyecto de un ciclo anterior, o es un dominio nuevo? Si continúa, indicar cuál:

## 3. Microservicios previstos y alcance esperado

**Regla de asignación:** cada integrante propone (o hereda) **dos microservicios**, no uno solo. De esos dos, **al menos uno debe ser transaccional** — una operación con cabecera-detalle real y al menos una regla de negocio verdadera, equivalente a `orden-ms` o `pago-ms` en `pagatu`. El segundo microservicio **no necesariamente** es transaccional — puede ser un CRUD simple, equivalente a `catalogo-ms` o `cliente-ms` en `pagatu`. Ningún integrante se queda con dos microservicios no transaccionales. Un microservicio puede compartirse entre dos integrantes si el equipo es grande y el alcance lo justifica — indícalo en la tabla.

| Integrante | Microservicio transaccional | Microservicio no transaccional |
|---|---|---|
| | | |
| | | |
| | | |

**Ficha por microservicio** — completa un bloque como este por cada microservicio de la tabla anterior (repite el bloque tantas veces como microservicios tenga el equipo):

### Microservicio: ______ (integrante: ______ · tipo: transaccional / no transaccional)

- Descripción breve (2-3 líneas): qué hace este microservicio y por qué existe en el flujo de negocio.
- Entidad principal (si no es transaccional, ej. `Categoria`/`Producto`) o cabecera-detalle (si es transaccional, ej. `Orden`/`DetalleOrden`):
- Datos iniciales previstos (tablas o entidades principales, con 2-3 atributos clave cada una):
- Endpoints iniciales previstos (mínimo 2, método + ruta, ej. `GET /api/v1/...`, `POST /api/v1/...`):
- ¿Se comunica con otro microservicio del equipo? ¿Cómo — síncrono (REST/Feign, S6) o asíncrono (eventos, S8)? Si todavía no lo sabes, escribe "por definir".
- Lista inicial de requisitos (mínimo 3, redactados como "el sistema debe..."):
    1.
    2.
    3.

### Microservicio: ______ (integrante: ______ · tipo: transaccional / no transaccional)

- Descripción breve (2-3 líneas):
- Entidad principal o cabecera-detalle:
- Datos iniciales previstos:
- Endpoints iniciales previstos:
- ¿Se comunica con otro microservicio del equipo? ¿Cómo?
- Lista inicial de requisitos:
    1.
    2.
    3.

*(repite este bloque por cada microservicio restante del equipo, hasta cubrir la tabla completa)*

- Qué SÍ cubre este proyecto en conjunto:
- Qué NO cubre — fuera de alcance, explícito:

**Pendiente para las siguientes sesiones:** la infraestructura compartida (Config Server, Eureka, Gateway, seguridad, mensajería, observabilidad) sigue el mismo patrón que enseña cada sesión sobre `pagatu` — no se declara aquí porque aplica igual sin importar el dominio de cada equipo; se construye en clase, sesión por sesión, sobre los microservicios de esta ficha.

## 4. Aprobación

- Docente:
- Fecha:
