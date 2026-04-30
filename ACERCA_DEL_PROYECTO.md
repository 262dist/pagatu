# Acerca del Proyecto: pagatu

## Objetivo

`pagatu` es una arquitectura base de microservicios para un sistema de pagos y comercio institucional. El proyecto busca ser profesional, didactico y evolutivo: incluye CRUD, comunicacion sincrona con Feign, mensajeria asincrona con Kafka, configuracion centralizada, descubrimiento de servicios, gateway, seguridad JWT, Docker y Kubernetes, sin caer en una arquitectura innecesariamente compleja.

La propuesta usa una arquitectura por capas simple, no hexagonal, pensada para mantener el codigo claro y facil de explicar.

## Stack Tecnico

- Java 17
- Spring Boot 3.5.x
- Maven
- Spring Web
- Spring Data JPA
- Spring Validation
- OpenFeign
- Eureka Client
- Config Client
- Spring Kafka
- MySQL 8
- Flyway
- Lombok
- MapStruct
- Keycloak como JWT Resource Server
- Docker y Docker Compose
- Kubernetes con manifiestos YAML

## Microservicios del Proyecto

El proyecto se plantea en releases para mantener una evolucion ordenada. No todos los microservicios se implementan desde el inicio: primero se construye el flujo principal y luego se agregan capacidades complementarias.

| Microservicio | Release | Responsabilidad |
|---|---:|---|
| `pagatu-ubigeo-ms` | 1 | Gestiona ubicaciones geograficas por pais: pais, departamento/region, provincia, distrito y codigos territoriales. |
| `pagatu-cliente-ms` | 1 | Gestiona clientes: alumno universitario, alumno colegio, apoderado y cliente externo. Permite CRUD, busqueda por documento y validacion de cliente. |
| `pagatu-catalogo-ms` | 1 | Gestiona lo que se puede comprar o pagar: productos, conceptos de pago, familias, categorias, tipos, precios y activacion. |
| `pagatu-orden-ms` | 1 | Es el nucleo del negocio. Crea ordenes, agrega items, calcula totales, consulta estados y lista ordenes por cliente. |
| `pagatu-pago-ms` | 1 | Gestiona el proceso financiero: registra, valida o rechaza pagos. |
| `pagatu-notificacion-ms` | 2 | Envia notificaciones por eventos relevantes, como orden creada, pago validado o pago rechazado. |
| `pagatu-contabilidad-ms` | 2 | Integra pagos validados con procesos contables, conciliacion y registros financieros internos. |
| `pagatu-reportes-ms` | 2 | Expone reportes operativos, financieros y analiticos sobre clientes, ordenes, pagos y catalogo. |

## Release 1: Core Inicial

| Microservicio | Responsabilidad | Comunicacion |
|---|---|---|
| `pagatu-ubigeo-ms` | Gestiona datos geograficos por pais para completar nacimiento, direccion o residencia del cliente. | REST |
| `pagatu-cliente-ms` | Gestiona clientes: alumno universitario, alumno colegio, apoderado y cliente externo. Permite CRUD, busqueda por documento y validacion de cliente. | REST y Feign |
| `pagatu-catalogo-ms` | Gestiona productos, conceptos de pago, familias, categorias, tipos, precios y activacion. | REST |
| `pagatu-orden-ms` | Es el nucleo del negocio. Crea ordenes, agrega items, calcula totales, consulta estados y lista ordenes por cliente. | REST, Feign y Kafka |
| `pagatu-pago-ms` | Gestiona el proceso financiero: registra, valida o rechaza pagos. | Kafka y REST |

## Infraestructura

| Componente | Funcion |
|---|---|
| `pagatu-config` | Centraliza la configuracion de los microservicios. |
| `pagatu-eureka` | Provee descubrimiento de servicios con Eureka. |
| `pagatu-gateway` | Es la entrada unica al sistema y enruta las peticiones a los microservicios. |

Rutas principales del Gateway:

```text
/api/clientes/**
/api/catalogo/**
/api/ubigeo/**
/api/ordenes/**
/api/pagos/**
```

## Flujo Principal

```text
Usuario
  -> Gateway
  -> pagatu-orden-ms
  -> Feign: pagatu-cliente-ms
  -> Feign interno: pagatu-cliente-ms -> pagatu-ubigeo-ms
  -> Feign: pagatu-catalogo-ms
  -> Kafka: orden.creada
  -> pagatu-pago-ms
  -> Kafka: pago.validado
```

`pagatu-orden-ms` consulta de forma sincrona a cliente y catalogo porque necesita respuestas inmediatas antes de crear una orden. `pagatu-cliente-ms` puede consultar a `pagatu-ubigeo-ms` para completar detalles de nacimiento, residencia o direccion. Si el cliente no es valido o el producto/concepto no existe, esta inactivo o no tiene precio valido, la orden no se crea. Una vez creada, publica el evento `orden.creada` para que `pagatu-pago-ms` continue el proceso de pago de forma asincrona.

## Regla de Comunicacion

Feign y Kafka se usan para problemas distintos:

- Feign se usa cuando el servicio necesita una respuesta inmediata para tomar una decision.
- Kafka se usa cuando algo ya ocurrio y otros servicios deben reaccionar.

En Release 1, Feign se usa para decisiones inmediatas: `pagatu-cliente-ms` consulta `pagatu-ubigeo-ms`, y `pagatu-orden-ms` consulta `pagatu-cliente-ms` y `pagatu-catalogo-ms`. `pagatu-ubigeo-ms` queda como servicio sincrono estable; no necesita evolucionar a Kafka. Kafka se usa entre `pagatu-orden-ms` y `pagatu-pago-ms`.

## Estructura General

```text
pagatu/
|-- infra/
|   |-- config/
|   |   |-- config-repo/
|   |   |-- src/
|   |   |-- pom.xml
|   |   `-- Dockerfile
|   |-- eureka/
|   |-- gateway/
|   |-- docker-compose.yml
|   |-- k8s-local/
|   `-- k8s/
|-- ubigeo-ms/
|   |-- src/
|   |-- pom.xml
|   |-- Dockerfile
|   |-- docker-compose-dev.yml
|   |-- docker-compose.yml
|   |-- k8s-local/
|   `-- k8s/
|-- cliente-ms/
|   |-- src/
|   |-- pom.xml
|   |-- Dockerfile
|   |-- docker-compose-dev.yml
|   |-- docker-compose.yml
|   |-- k8s-local/
|   `-- k8s/
|-- catalogo-ms/
|   |-- src/
|   |-- pom.xml
|   |-- Dockerfile
|   |-- docker-compose-dev.yml
|   |-- docker-compose.yml
|   |-- k8s-local/
|   `-- k8s/
|-- orden-ms/
|   |-- src/
|   |-- pom.xml
|   |-- Dockerfile
|   |-- docker-compose-dev.yml
|   |-- docker-compose.yml
|   |-- k8s-local/
|   `-- k8s/
`-- pago-ms/
    |-- src/
    |-- pom.xml
    |-- Dockerfile
    |-- docker-compose-dev.yml
    |-- docker-compose.yml
    |-- k8s-local/
    `-- k8s/
```

## Convencion de Nombres

Las carpetas del repositorio usan nombres cortos para facilitar el trabajo diario. Los artefactos operativos usan el prefijo `pagatu-` para evitar colisiones en Docker, Eureka, Config Server y Kubernetes.

| Elemento | Ejemplo |
|---|---|
| Carpeta del proyecto | `cliente-ms` |
| Maven `artifactId` | `pagatu-cliente-ms` |
| `spring.application.name` | `pagatu-cliente-ms` |
| Imagen Docker | `pagatu-cliente-ms:latest` |
| Servicio Docker Compose | `pagatu-cliente-ms` |
| Deployment Kubernetes | `pagatu-cliente-ms` |
| Service Kubernetes | `pagatu-cliente-ms` |
| Config Server file | `pagatu-cliente-ms.yml` |
| Base de datos | `pagatu_cliente_db` |
| Paquete Java | `com.pagatu.cliente` |
| Ruta Gateway | `/api/clientes/**` |

## Estructura Interna por Microservicio

Cada microservicio core debe seguir una estructura por capas simple:

```text
com.pagatu.<contexto>
|-- config
|-- controller
|-- dto
|   |-- request
|   `-- response
|-- entity
|-- exception
|-- filter
|-- mapper
|-- repository
|-- service
|   `-- impl
|-- client
|-- event
`-- <Contexto>Application.java
```

Las carpetas `client` y `event` solo se crean cuando el microservicio las necesita:

| Microservicio | `client` | `event` |
|---|---:|---:|
| `pagatu-ubigeo-ms` | No | No |
| `pagatu-cliente-ms` | Si | No |
| `pagatu-catalogo-ms` | No | No |
| `pagatu-orden-ms` | Si | Si |
| `pagatu-pago-ms` | No | Si |

## Configuracion Comun

En los microservicios se considera la siguiente configuracion base:

- `SecurityConfig.java` para proteger endpoints con JWT.
- `JwtProperties.java` para centralizar propiedades de seguridad.
- `FeignTraceConfig.java` para propagar trazabilidad en llamadas Feign.
- `OpenApiConfig.java` para documentacion de API.
- `KafkaConfig.java` solo cuando el servicio use Kafka.
- `CorrelationIdFilter.java` para leer o generar `X-Trace-ID`, guardarlo en MDC y retornarlo en la respuesta.
- `logback-spring.xml` con `traceId=%X{X-Trace-ID}` para trazabilidad en logs.

No se crea carpeta `security`; la configuracion de seguridad vive dentro de `config`.

## Bases de Datos

Cada microservicio core tiene su propia base de datos:

| Microservicio | Base de datos |
|---|---|
| `pagatu-ubigeo-ms` | `pagatu_ubigeo_db` |
| `pagatu-cliente-ms` | `pagatu_cliente_db` |
| `pagatu-catalogo-ms` | `pagatu_catalogo_db` |
| `pagatu-orden-ms` | `pagatu_orden_db` |
| `pagatu-pago-ms` | `pagatu_pago_db` |

Las migraciones se gestionan con Flyway mediante `V1__init.sql`.

## Seguridad

Los servicios se configuran como Resource Server usando JWT emitidos por Keycloak. Los endpoints deben validar autenticacion y roles segun la responsabilidad de cada microservicio.

## Docker y Kubernetes

El proyecto contempla:

- `Dockerfile` por microservicio y componente de infraestructura.
- `docker-compose.yml` en `infra/` para infraestructura compartida o escenarios integrados.
- `docker-compose-dev.yml` por microservicio para levantar dependencias locales del MS, por ejemplo MySQL, mientras la aplicacion corre con Java local.
- `docker-compose.yml` por microservicio para validar su imagen Docker de forma aislada con dependencias minimas.
- `k8s-local/` para Minikube o Kubernetes local.
- `k8s/` para nube o produccion real.

El Gateway es la entrada externa; los microservicios no deben consumirse directamente por puertos publicos.

## Alcance de Release 1

Release 1 incluye los 5 microservicios core necesarios para explicar y validar el flujo principal:

- CRUD de clientes.
- CRUD/consulta de ubigeo por pais para completar nacimiento, residencia o direccion del cliente.
- CRUD de catalogo: productos, conceptos, familias, categorias, tipos y precios.
- Creacion y consulta de ordenes.
- Validacion sincrona con Feign desde cliente hacia ubigeo, y desde orden hacia cliente y catalogo.
- Publicacion de `orden.creada`.
- Consumo de `orden.creada` desde pago.
- Publicacion de `pago.validado`.
- Configuracion centralizada, Eureka, Gateway, seguridad, trazabilidad, Docker y Kubernetes.

## Pendiente para Release 2

No se incluyen por ahora:

- `pagatu-notificacion-ms`
- `pagatu-contabilidad-ms`
- `pagatu-inventario-ms`
- `pagatu-reportes-ms`

Estos servicios pueden agregarse cuando el flujo principal este estable y sea necesario cubrir mensajes, integracion contable, inventario, reservas reales de stock o analitica.
