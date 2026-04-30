# pagatu

`pagatu` es un proyecto de microservicios para pagos y comercio institucional. La arquitectura se construye por sesiones, empezando con un microservicio base independiente y evolucionando hacia configuracion centralizada, descubrimiento, gateway, Feign, resiliencia, Kafka, seguridad, Docker, Kubernetes y una aplicacion Angular.

## Documentos del Proyecto

| Documento | Rol |
|---|---|
| `README.md` | Entrada principal del repositorio. Resume el proposito del proyecto, el orden de trabajo por sesiones y donde leer cada tema. |
| `ACERCA_DEL_PROYECTO.md` | Describe la vision concreta de Pagatu: microservicios, releases, flujo principal, infraestructura, stack tecnico y estructura real del repositorio. |
| `README_ESTRUCTURA_ESTANDAR.md` | Define el patron generico de estructura para proyectos de microservicios, sin depender de nombres especificos de Pagatu. |

## Arquitectura

Pagatu se organiza como una arquitectura de microservicios con entrada unica por Gateway, configuracion centralizada, descubrimiento de servicios, comunicacion sincrona con Feign, comunicacion asincrona con Kafka y observabilidad con Prometheus, Loki y Grafana.

En Release 1, el flujo principal se apoya en estos servicios:

- `cliente-ms`: gestiona clientes, documentos, contacto y datos de nacimiento.
- `ubigeo-ms`: provee datos geograficos por pais para completar informacion del cliente.
- `catalogo-ms`: gestiona productos, conceptos de pago, familias, categorias, tipos y precios.
- `orden-ms`: orquesta la creacion de ordenes; valida cliente y catalogo por Feign.
- `pago-ms`: procesa eventos de orden creada y publica resultados de pago.
- `auth-ms`: gestiona autenticacion y control de acceso inicial sin depender aun de Keycloak.

La comunicacion queda dividida por responsabilidad:

- Gateway enruta las llamadas externas hacia los MS.
- Config Server entrega configuracion centralizada desde `config-repo`.
- Eureka permite descubrimiento de servicios.
- Feign se usa cuando un MS necesita respuesta inmediata.
- Circuit Breaker protege llamadas Feign ante fallos o latencia.
- Kafka se usa cuando algo ya ocurrio y otros servicios deben reaccionar.
- Prometheus recolecta metricas, Loki centraliza logs y Grafana visualiza ambos.
- Angular consume el sistema siempre por Gateway.

### Diagrama 0: C4 Nivel 1 - Contexto

```mermaid
flowchart LR
    usuario["Person: Usuario institucional"]
    admin["Person: Administrador"]
    angular["Software System: Angular App"]
    pagatu["Software System: Pagatu Platform"]
    auth["Container: auth-ms (luego Keycloak)"]
    correo["External System: Servicios externos opcionales"]

    usuario -->|usa| angular
    admin -->|administra| angular
    angular -->|consume API| pagatu
    angular -->|autenticacion| auth
    auth -->|autoriza acceso| pagatu
    pagatu -. notificaciones futuras .-> correo
```

Este nivel muestra el sistema como una caja principal: Angular es la interfaz de usuario y Pagatu Platform agrupa Gateway, microservicios, configuracion, registro y observabilidad. Kafka se trata como plataforma compartida de mensajeria, no como componente exclusivo del proyecto.

### Diagrama 1: Cliente y Ubigeo

```mermaid
flowchart LR
    usuario[Usuario] --> angular[Angular App]
    angular --> gateway[Gateway]

    gateway --> cliente[cliente-ms]
    gateway --> ubigeo[ubigeo-ms]

    cliente -- Feign + Circuit Breaker --> ubigeo

    config[Config Server] --> configrepo[(config-repo)]
    cliente -. config .-> config
    ubigeo -. config .-> config
    gateway -. config .-> config

    eureka[Eureka] -. registro .- cliente
    eureka -. registro .- ubigeo
    eureka -. registro .- gateway
```

### Diagrama 2: Orden y Pago

```mermaid
flowchart LR
    gateway[Gateway] --> orden[orden-ms]
    gateway --> pago[pago-ms]

    orden[orden-ms] -- publica orden.creada --> kafka[(Kafka)]
    kafka -- consume orden.creada --> pago[pago-ms]
    pago -- publica pago.validado --> kafka

    config[Config Server] --> configrepo[(config-repo)]
    orden -. config .-> config
    pago -. config .-> config
    gateway -. config .-> config

    eureka[Eureka] -. registro .- orden
    eureka -. registro .- pago
    eureka -. registro .- gateway
```

### Diagrama 3: Release 1 Completo

```mermaid
flowchart LR
    usuario[Usuario] --> angular[Angular App]
    angular --> gateway[Gateway]

    gateway --> cliente[cliente-ms]
    gateway --> ubigeo[ubigeo-ms]
    gateway --> catalogo[catalogo-ms]
    gateway --> orden[orden-ms]
    gateway --> pago[pago-ms]
    gateway --> auth[auth-ms]

    cliente -- Feign + Circuit Breaker --> ubigeo
    orden -- Feign + Circuit Breaker --> cliente
    orden -- Feign + Circuit Breaker --> catalogo

    orden -- publica orden.creada --> kafka[(Kafka)]
    kafka -- consume orden.creada --> pago
    pago -- publica pago.validado --> kafka

    config[Config Server] --> configrepo[(config-repo)]
    auth -. config .-> config
    cliente -. config .-> config
    ubigeo -. config .-> config
    catalogo -. config .-> config
    orden -. config .-> config
    pago -. config .-> config
    gateway -. config .-> config

    eureka[Eureka] -. registro .- auth
    eureka -. registro .- cliente
    eureka -. registro .- ubigeo
    eureka -. registro .- catalogo
    eureka -. registro .- orden
    eureka -. registro .- pago
    eureka -. registro .- gateway

    prometheus[Prometheus] -. metrics .-> auth
    prometheus -. metrics .-> gateway
    prometheus -. metrics .-> cliente
    prometheus -. metrics .-> ubigeo
    prometheus -. metrics .-> catalogo
    prometheus -. metrics .-> orden
    prometheus -. metrics .-> pago

    loki[Loki] -. logs .-> auth
    loki -. logs .-> gateway
    loki -. logs .-> cliente
    loki -. logs .-> ubigeo
    loki -. logs .-> catalogo
    loki -. logs .-> orden
    loki -. logs .-> pago

    grafana[Grafana] --> prometheus
    grafana --> loki
```

### Diagrama 4: Ambientes de Ejecucion

```mermaid
flowchart TB
    subgraph dev[DEV local]
        devjava[MS con Java 17 local]
        devinfra[Infra con Java 17 local]
        devmscompose[docker-compose-dev.yml del MS]
        devshared[Config + Eureka + Gateway]
        devmysql[(MySQL por MS en Docker)]
        devinfra --> devshared
        devmscompose --> devmysql
        devjava --> devshared
        devjava --> devmysql
    end

    subgraph compose[PROD local - Docker Compose]
        subgraph composeinfraBox[Infra compartida]
            infracompose[infra/docker-compose.yml]
            composeinfra[Config + Eureka + Gateway]
            infracompose --> composeinfra
        end

        subgraph messagingBox[Mensajeria compartida]
            kafkacompose[platform/kafka-compose.yml]
            composekafka[(Kafka)]
            kafkacompose --> composekafka
        end

        subgraph observabilityBox[Observabilidad compartida]
            obscompose[platform/observability-compose.yml]
            obsstack[Prometheus + Loki + Grafana]
            obscompose --> obsstack
        end

        subgraph composemsBox[Microservicio]
            mscompose[docker-compose.yml del MS]
            composems[MS como contenedor Java 17]
            composedb[(MySQL del MS)]
            mscompose --> composems
            mscompose --> composedb
        end

        composems --> composeinfra
        composems --> composekafka
        composems --> obsstack
    end

    subgraph k8slocal[Kubernetes local - Minikube]
        k8sdeploy[Deployments de MS]
        k8ssvc[Services ClusterIP]
        k8sdb[(MySQL en cluster local)]
        k8sing[Gateway o Ingress local]
        k8sing --> k8ssvc
        k8ssvc --> k8sdeploy
        k8sdeploy --> k8sdb
    end

    subgraph cloud[Nube / Produccion real]
        cloudk8s[Kubernetes administrado]
        registry[Container Registry]
        manageddb[(Base de datos administrada)]
        managedkafka[(Kafka administrado o Strimzi)]
        ingress[Ingress / Load Balancer]
        registry --> cloudk8s
        ingress --> cloudk8s
        cloudk8s --> manageddb
        cloudk8s --> managedkafka
    end

    dev --> compose --> k8slocal --> cloud
```

En DEV local, los microservicios y la infraestructura (`config`, `eureka`, `gateway`) corren con Java 17 en la maquina del desarrollador. Docker se usa para dependencias propias de cada MS, como MySQL, mediante `<ms>/docker-compose-dev.yml`. En PROD local, la infraestructura compartida se levanta con `infra/docker-compose.yml`; Kafka y el stack de observabilidad se tratan como plataformas compartidas; cada microservicio valida su imagen con su propio `<ms>/docker-compose.yml`. En Kubernetes local se usan manifiestos `k8s-local/`; en nube se usan imagenes desde registry, servicios administrados y manifiestos `k8s/`.

## Ruta de Trabajo por Sesiones

| Sesion | Trabajo principal | Tarea para avanzar |
|---:|---|---|
| 1 | Crear `cliente-ms` como microservicio base independiente: capas simples, CRUD, MySQL, Flyway, validaciones, DTOs, MapStruct, logs, OpenAPI, `CorrelationIdFilter`, `Dockerfile`, `docker-compose-dev.yml` y `docker-compose.yml`. Se registra y despliega como `pagatu-cliente-ms`. | Crear `ubigeo-ms` con pais, departamento/region, provincia y distrito. Validar su MySQL con `docker-compose-dev.yml` y preparar su `Dockerfile`. |
| 2 | Crear `config` y `config-repo`. Mover configuracion de `cliente-ms` al Config Server y preparar archivos por servicio. Validar imagen Docker de `config`. | Configurar `ubigeo-ms` para leer desde Config Server y validar su imagen Docker. |
| 3 | Crear `eureka` y `gateway`. Registrar `cliente-ms` en Eureka y exponer `/api/clientes/**` por Gateway. Validar imagenes Docker de `eureka` y `gateway`. | Integrar `ubigeo-ms` con Config Server, Eureka y Gateway. Exponer `/api/ubigeo/**`. |
| 4 | Implementar Feign + Circuit Breaker: `cliente-ms` consulta `ubigeo-ms`. Agregar timeouts, fallback, manejo de errores y propagacion de `X-Trace-ID`. | Crear `catalogo-ms` como servicio REST estable de productos, conceptos, familias, categorias, tipos y precios, con `docker-compose-dev.yml`, `Dockerfile`, Feign + Circuit Breaker listo para `orden-ms`. |
| 5 | Incorporar observabilidad con Actuator, Prometheus, Loki y Grafana. Exponer health, metrics, logs y dashboards basicos para `gateway`, `cliente-ms`, `ubigeo-ms` y `catalogo-ms`. | Preparar contratos de orden, evento `orden.creada` y archivos Docker iniciales para `orden-ms` y `pago-ms`. |
| 6 | Crear `orden-ms` y `pago-ms`. Implementar Event-Driven con Kafka: `orden-ms` valida cliente y catalogo por Feign, crea la orden, publica `orden.creada`; `pago-ms` consume `orden.creada` y publica `pago.validado`. Validar Kafka en Docker local. | Revisar idempotencia basica, manejo de eventos duplicados, trazabilidad en consumidores e imagenes Docker de ambos MS. |
| 7 | Crear `auth-ms` e implementar control de acceso inicial sin Keycloak obligatorio: autenticacion basica/JWT propio, roles logicos, permisos por endpoint y reglas de autorizacion en Gateway y MS. Preparar `SecurityConfig` para evolucionar a Keycloak. | Dejar Keycloak como modulo opcional y documentar roles/claims esperados. |
| 8 | Preparar `k8s-local/` y `k8s/` para infraestructura y microservicios, diferenciando Minikube de nube/produccion real. | Verificar nombres operativos `pagatu-*`, perfiles, variables, secrets/configmaps y tags de GitHub por sesion/release. |
| 9 | Crear aplicacion Angular base para consumir Gateway: layout, rutas, servicios HTTP, interceptores, pantallas de clientes, catalogo y consulta de ordenes. | Preparar formularios para crear orden y visualizar estado de pago. |
| 10 | Completar Angular: flujo de creacion de orden, visualizacion de pago, manejo de errores, trazabilidad con `X-Trace-ID` y build Docker del frontend. | Dejar preparado un modulo opcional de login con Keycloak. |

## Temas Transversales Pendientes

Ademas de las sesiones principales, el proyecto debe considerar:

- Resiliencia: Resilience4j, timeouts, circuit breakers, retries limitados y fallbacks.
- Observabilidad: logs con `X-Trace-ID`, Actuator, health checks, Prometheus, Loki y Grafana.
- Documentacion API: OpenAPI/Swagger por MS, consumido preferentemente por Gateway.
- Manejo de errores: excepciones controladas, codigos HTTP consistentes y respuestas estandar.
- Testing: unit tests, integration tests y pruebas de contrato para Feign/eventos cuando el flujo madure.
- Versionado de eventos: nombres de topicos, esquema de payloads y compatibilidad de eventos Kafka.
- Perfiles: `dev`, `prod`, Docker local, Kubernetes local y nube.
- Frontend: Angular, consumo por Gateway, manejo de errores, trazabilidad y build Docker.
- Seguridad opcional: Keycloak, JWT Resource Server, roles, claims y vinculacion con `keycloakUserId`.

## Modulo Opcional: Keycloak

Si el curso necesita una ampliacion de seguridad, Keycloak puede trabajarse como modulo opcional despues de Release 1:

- Crear realm, clients, roles y usuarios.
- Configurar Gateway y MS como Resource Server.
- Validar JWT y roles por endpoint.
- Vincular `cliente-ms` con `keycloakUserId` usando el claim `sub`.
- Ajustar Angular para login, refresh de token e interceptor `Authorization`.

## Release 1

Release 1 construye el core inicial:

Carpetas del repositorio:

- `cliente-ms`
- `auth-ms`
- `catalogo-ms`
- `ubigeo-ms`
- `orden-ms`
- `pago-ms`
- `infra/config`
- `infra/eureka`
- `infra/gateway`

Artefactos operativos:

- `pagatu-cliente-ms`
- `pagatu-auth-ms`
- `pagatu-catalogo-ms`
- `pagatu-ubigeo-ms`
- `pagatu-orden-ms`
- `pagatu-pago-ms`
- `pagatu-config`
- `pagatu-eureka`
- `pagatu-gateway`

## Lectura Recomendada

1. Leer `ACERCA_DEL_PROYECTO.md` para entender que se va a construir.
2. Leer `README_ESTRUCTURA_ESTANDAR.md` para entender el patron de carpetas.
3. Trabajar las sesiones en orden, empezando por un microservicio independiente antes de agregar infraestructura.
