# Brief Técnico del Proyecto Sello

Este documento es el hito de **S2** del cronograma (ver [Guía del Proyecto Sello](index.md), sección 4): el punto donde el equipo declara, por escrito, qué sistema distribuido va a construir durante el ciclo — antes de avanzar a S3 (registro y descubrimiento). No reemplaza el informe final; es la ficha corta que fija el rumbo desde el principio.

`pagatu` es un entorno integrado para construir un sistema distribuido de **comercio electrónico** — ese es el dominio del proyecto, no uno libre. Las guías de S1 en adelante muestran **cómo** se construye cada capacidad distribuida (Config Server, Eureka, Gateway, seguridad, mensajería, observabilidad) sobre ese dominio. Tu equipo **adapta** el mismo sistema a su propio proyecto final: un rubro o nicho propio de comercio electrónico (no necesariamente el mismo catálogo de `pagatu`, y no necesariamente con los mismos nombres de microservicio `catalogo-ms`/`orden-ms`/etc.), pero manteniendo el mismo flujo de negocio de fondo — catálogo, orden y al menos un **servicio externo real**, no simulado (de preferencia pagos en línea; ver sección 2). El repositorio se identifica con su propio `grupo-<numero>-<nombre-proyecto>` en los topics (ver [Guía del Proyecto Sello](index.md), sección 5).

Cada equipo llena una sola copia de este brief, la publica en su repositorio (o en su MkDocs) y la actualiza solo si el alcance cambia de verdad — no en cada sesión.

## 1. Datos del equipo

- Nombre del equipo:
- Sección:
- Repositorio (URL):
- Topics del repositorio configurados (sí/no) — incluye `grupo-<numero>-<nombre-proyecto>` (ver [Guía del Proyecto Sello](index.md), sección 5):

**Integrantes:**

| Integrante | Rol o énfasis previsto (ej. seguridad, mensajería, frontend) |
|---|---|
| | |
| | |
| | |
| | |

## 2. Dominio del proyecto

- Nombre del proyecto:
- Problema o necesidad que resuelve (2-4 líneas):
- Dominio de negocio (breve — rubro o nicho de comercio electrónico, ej. ropa, comida, servicios, entradas, y el flujo de extremo a extremo: catálogo → orden → pago, igual que en `pagatu`):
- Usuarios / actores principales (roles que interactúan con el sistema):
- Servicio externo real que integra el proyecto (la API real, no simulada) — de preferencia una pasarela de pagos (ej. Mercado Pago, económica y con sandbox gratuito); si el rubro no encaja con pagos en línea, puede ser otro servicio externo real de complejidad equivalente (Google Maps, predicción de tiempos, un LLM, RENIEC/SUNAT, etc.). Indica cuál:
- ¿Continúa un proyecto de un ciclo anterior, o es un dominio nuevo? Si continúa, indicar cuál:

## 3. Microservicios previstos y alcance esperado

**Regla de asignación:** cada integrante propone (o hereda) **dos microservicios**, no uno solo. De esos dos, **al menos uno debe ser transaccional** — una operación con cabecera-detalle real y al menos una regla de negocio verdadera, equivalente a `orden-ms` o `pago-ms` en `pagatu`. El segundo microservicio **no necesariamente** es transaccional — puede ser un CRUD simple, equivalente a `catalogo-ms` o `cliente-ms` en `pagatu`. Ningún integrante se queda con dos microservicios no transaccionales. Un microservicio puede compartirse entre dos integrantes si el equipo es grande y el alcance lo justifica — indícalo en la tabla.

Un microservicio de **pagos** (equivalente a `pago-ms`), integrando una pasarela real, es lo recomendado y cuenta como el microservicio transaccional de quien lo tome. Si el rubro del equipo no encaja bien con pagos en línea, puede reemplazarse por otro microservicio que integre **otro servicio externo real** de complejidad equivalente (sección 2) — Google Maps/geolocalización, predicción de tiempos, un LLM, etc. — siempre que también sea real, no simulado: el equipo debe poder mostrar la llamada real a la API externa, no un mock que responde al instante.

Un microservicio (o componente) de **seguridad** (equivalente a `auth-ms`) también es obligatorio en el equipo — de preferencia [Keycloak](https://www.keycloak.org/) (Identity Provider ya armado, gratuito), aunque puede construirse un servicio de autenticación propio (JWT con Spring Security, como hace `pagatu` en clase antes de reemplazarlo por Keycloak) si el equipo tiene una razón concreta. Este microservicio **no cuenta como el módulo transaccional** de quien lo tome — es no transaccional, como `cliente-ms` o `catalogo-ms`.

**La seguridad no es responsabilidad exclusiva de este microservicio**: todos los microservicios del equipo, sin excepción, deben validar el token/JWT en sus rutas protegidas y aplicar los roles o permisos que correspondan — no solo el que construye el Identity Provider. Se declara aquí porque afecta a cada ficha de la tabla, no solo a una.

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
- ¿Qué rutas quedan protegidas y con qué rol(es)? (todo microservicio valida el token, no solo el de seguridad — si algún endpoint es intencionalmente público, indícalo también):
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
- ¿Qué rutas quedan protegidas y con qué rol(es)?
- Lista inicial de requisitos:
    1.
    2.
    3.

*(repite este bloque por cada microservicio restante del equipo, hasta cubrir la tabla completa)*

- Qué SÍ cubre este proyecto en conjunto:
- Qué NO cubre — fuera de alcance, explícito:

**Pendiente para las siguientes sesiones:** la infraestructura compartida (Config Server, Eureka, Gateway, mensajería, observabilidad) sigue el mismo patrón que enseña cada sesión sobre `pagatu` — no se declara aquí porque aplica igual sin importar el dominio de cada equipo; se construye en clase, sesión por sesión, sobre los microservicios de esta ficha. La seguridad es la excepción: se planifica desde ahora (arriba) porque, a diferencia del resto, es responsabilidad de todos los microservicios desde el diseño, no algo que se agrega recién en S7.

## 4. Aprobación

- Docente:
- Fecha:
