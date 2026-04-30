# Estructura Estandar del Proyecto

Este documento define un patron de organizacion para proyectos de microservicios. La idea es separar con claridad la infraestructura compartida, los microservicios de negocio y los manifiestos de despliegue por ambiente.

## Estructura General Generica

```text
<proyecto>/
|-- infra/
|   |-- <config>/
|   |   |-- config-repo/
|   |   |-- src/
|   |   |-- pom.xml
|   |   `-- Dockerfile
|   |-- <registry>/
|   |-- <gateway>/
|   |-- docker-compose.yml
|   |-- k8s-local/
|   `-- k8s/
|-- <microservicio-a>/
|   |-- src/
|   |-- pom.xml
|   |-- Dockerfile
|   |-- docker-compose-dev.yml
|   |-- docker-compose.yml
|   |-- k8s-local/
|   `-- k8s/
|-- <microservicio-b>/
|   |-- src/
|   |-- pom.xml
|   |-- Dockerfile
|   |-- docker-compose-dev.yml
|   |-- docker-compose.yml
|   |-- k8s-local/
|   `-- k8s/
`-- <microservicio-n>/
    |-- src/
    |-- pom.xml
    |-- Dockerfile
    |-- docker-compose-dev.yml
    |-- docker-compose.yml
    |-- k8s-local/
    `-- k8s/
```

## Fundamento

- `infra/` contiene la plataforma compartida: Config Server, Eureka, Gateway, Kafka, Docker Compose y manifiestos Kubernetes de infraestructura.
- `infra/docker-compose.yml` valida escenarios integrados de infraestructura y plataforma compartida.
- Cada microservicio tiene su propio `docker-compose-dev.yml` para levantar solo sus dependencias locales y su propio `docker-compose.yml` para validar la imagen del MS de forma aislada.
- Los microservicios core viven directamente en la raiz para reducir anidamiento y facilitar el trabajo diario.
- `config-repo/` vive dentro de `infra/<config>/` porque es la fuente local de configuracion que usa directamente el Config Server.
- Cada microservicio tiene su propio `k8s-local/` y `k8s/` para separar despliegue local de despliegue productivo.
- `k8s-local/` representa Minikube o Kubernetes local, donde se pueden incluir dependencias como MySQL.
- `k8s/` representa nube o produccion real, donde las dependencias criticas suelen ser servicios administrados o externos.

## Convencion de Nombres

Se recomienda usar carpetas cortas en el repositorio y nombres con prefijo del proyecto para artefactos desplegables.

| Elemento | Convencion |
|---|---|
| Carpeta del microservicio | `<contexto>-ms` |
| Carpeta de infraestructura | `<componente>` |
| Maven `artifactId` | `<proyecto>-<contexto>-ms` |
| `spring.application.name` | `<proyecto>-<contexto>-ms` |
| Imagen Docker | `<proyecto>-<contexto>-ms:tag` |
| Service/Deployment Kubernetes | `<proyecto>-<contexto>-ms` |
| Archivo Config Server | `<proyecto>-<contexto>-ms.yml` |
| Base de datos | `<proyecto>_<contexto>_db` |
| Paquete Java | `com.<organizacion>.<contexto>` |

## Config Server y Config Repo

`config-repo` no se compila con el Config Server; solo contiene archivos `.yml` que el Config Server publica. Al estar dentro de `infra/<config>/`, se evita depender de rutas absolutas como `C:/...` y se facilita el uso de rutas relativas o variables de entorno.

Ejemplo recomendado para ejecucion local:

```yaml
spring:
  cloud:
    config:
      server:
        native:
          search-locations: ${CONFIG_REPO_LOCATION:file:./config-repo}
```

Ejemplo recomendado para Docker:

```yaml
volumes:
  - ./<config>/config-repo:/config-repo
environment:
  SPRING_PROFILES_ACTIVE: native
  CONFIG_REPO_LOCATION: file:/config-repo
```

## Kubernetes de Infraestructura

Para infraestructura se recomienda un bloque Kubernetes compartido, porque estos componentes forman la plataforma base del sistema.

```text
infra/
|-- k8s-local/
|   |-- 00-namespace.yml
|   |-- config/
|   |   |-- 01-configmap.yml
|   |   |-- 02-deployment.yml
|   |   `-- 03-service.yml
|   |-- eureka/
|   |   |-- 01-configmap.yml
|   |   |-- 02-deployment.yml
|   |   `-- 03-service.yml
|   |-- gateway/
|   |   |-- 01-configmap.yml
|   |   |-- 02-deployment.yml
|   |   `-- 03-service.yml
|   `-- kafka/
|       |-- 01-zookeeper.yml
|       |-- 02-kafka.yml
|       `-- 03-kafka-ui.yml
`-- k8s/
    |-- 00-namespace.yml
    |-- config/
    |-- eureka/
    |-- gateway/
    `-- kafka/
```

En `infra/k8s-local/` se pueden incluir Kafka, Zookeeper y Kafka UI dentro del cluster local. En `infra/k8s/`, para nube real, Kafka puede reemplazarse por un servicio administrado o por una instalacion especializada como Strimzi.

## Microservicio Core

Cada microservicio de negocio mantiene su codigo, Dockerfile y manifiestos cerca de su propio contexto.

```text
<microservicio>/
|-- src/
|-- pom.xml
|-- Dockerfile
|-- docker-compose-dev.yml
|-- docker-compose.yml
|-- k8s-local/
`-- k8s/
```

En cada microservicio:

- `docker-compose-dev.yml` levanta dependencias del MS para desarrollo, por ejemplo su MySQL. La aplicacion corre con Java local.
- `docker-compose.yml` levanta la imagen Docker del MS y sus dependencias minimas para validar ejecucion local tipo produccion.
- El compose de `infra/` sirve para levantar infraestructura compartida o escenarios integrados.

La estructura interna Java debe ser simple y por capas:

```text
com.<organizacion>.<contexto>
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

Las carpetas `client` y `event` solo se crean si el microservicio las necesita.

| Tipo de microservicio | `client` | `event` |
|---|---:|---:|
| REST simple | No | No |
| Servicio sincrono consumido por Feign | No | No |
| Orquestador hibrido | Si | Si |
| Servicio event-driven | No | Si |

## Kubernetes por Microservicio

Para Kubernetes local:

```text
<microservicio>/
`-- k8s-local/
    |-- 01-configmap.yml
    |-- 02-secret.yml
    |-- 03-mysql.yml
    |-- 04-deployment.yml
    `-- 05-service.yml
```

`k8s-local` incluye MySQL porque busca levantar el microservicio completo en Minikube o un cluster local.

Para Kubernetes en nube o produccion real:

```text
<microservicio>/
`-- k8s/
    |-- 01-configmap.yml
    |-- 02-secret.yml
    |-- 03-deployment.yml
    `-- 04-service.yml
```

`k8s` no incluye MySQL del microservicio como pod propio. En produccion, la base de datos debe ser administrada o externa al ciclo de vida del microservicio.

## Regla Practica

```text
infra/k8s-local/       -> plataforma completa para Minikube
infra/k8s/             -> plataforma para nube o produccion
<ms>/k8s-local/        -> despliegue local del MS con dependencias
<ms>/k8s/              -> despliegue productivo del MS
```

Esta separacion evita mezclar escenarios locales con produccion real y reduce el riesgo de llevar manifiestos de prueba a un cluster productivo.
