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
- `pago-ms`: procesa eventos de orden creada, integra una pasarela externa como Niubiz o Culqi y publica resultados de pago.
- `auth-ms`: gestiona autenticacion y control de acceso inicial sin depender aun de Keycloak.

La comunicacion queda dividida por responsabilidad:

- Gateway enruta las llamadas externas hacia los MS.
- Config Server entrega configuracion centralizada desde `config-repo`.
- Eureka permite descubrimiento de servicios.
- Feign se usa cuando un MS necesita respuesta inmediata.
- Circuit Breaker protege llamadas Feign ante fallos o latencia.
- Kafka se usa cuando algo ya ocurrio y otros servicios deben reaccionar.
- `pago-ms` encapsula la integracion con proveedores externos de pago para no acoplar el resto del sistema a Niubiz, Culqi u otro proveedor.
- Prometheus recolecta metricas, Loki centraliza logs y Grafana visualiza ambos.
- Angular consume el sistema siempre por Gateway.

### C4 Nivel 1: Contexto del Sistema

```mermaid
flowchart LR
    auth["Software System: Auth institucional (auth-ms, luego Keycloak)"]
    usuario["Person: Usuario institucional"]
    pagatu["Software System: Pagatu Platform"]
    kafkaCorp["External System: Kafka empresarial compartido"]
    pasarela["External System: Pasarela de pago (Niubiz/Culqi)"]

    usuario -->|se identifica| auth
    usuario -->|usa| pagatu
    auth -->|entrega identidad y roles| pagatu
    pagatu -->|publica/consume eventos| kafkaCorp
    pagatu -->|procesa pago| pasarela
```

Este nivel muestra Pagatu como una caja principal frente a su actor institucional y sistemas compartidos. `auth-ms` aparece aqui porque representa la identidad institucional transversal: el mismo punto de autenticacion puede servir a Pagatu y tambien a otros modulos de la empresa, como planes de estudios, carga academica, matricula, contabilidad o LMS. En Release 1 se implementa como `auth-ms`; luego puede evolucionar a Keycloak sin cambiar la idea arquitectonica. Los detalles internos de Pagatu, como Angular, Gateway, microservicios, Config Server y Eureka, aparecen recien en el nivel de contenedores. Kafka se trata como plataforma empresarial compartida, no como componente exclusivo del proyecto.

### C4 Nivel 2: Software System - Pagatu Platform

Contenedores de Release 1.

```mermaid
flowchart LR
    usuario["Person: Usuario institucional"] --> angular["Container: Angular App"]
    angular --> gateway["Container: Gateway"]

    gateway --> cliente["Container: cliente-ms"]
    gateway --> ubigeo["Container: ubigeo-ms"]
    gateway --> catalogo["Container: catalogo-ms"]
    gateway --> orden["Container: orden-ms"]
    gateway --> pago["Container: pago-ms"]
    gateway --> auth["Container: auth-ms"]

    cliente -- Feign + Circuit Breaker --> ubigeo
    orden -- Feign + Circuit Breaker --> cliente
    orden -- Feign + Circuit Breaker --> catalogo

    orden -- publica orden.creada --> kafka["External System: Kafka empresarial compartido"]
    kafka -- consume orden.creada --> pago
    pago -- publica pago.validado --> kafka
    pago -- autoriza/captura pago --> pasarela["External System: Pasarela de pago (Niubiz/Culqi)"]

    config["Container: Config Server"] --> configrepo[(Data Store: config-repo)]
    auth -. config .-> config
    cliente -. config .-> config
    ubigeo -. config .-> config
    catalogo -. config .-> config
    orden -. config .-> config
    pago -. config .-> config
    gateway -. config .-> config

    eureka["Container: Eureka"] -. registro .- auth
    eureka -. registro .- cliente
    eureka -. registro .- ubigeo
    eureka -. registro .- catalogo
    eureka -. registro .- orden
    eureka -. registro .- pago
    eureka -. registro .- gateway

    prometheus["External System: Prometheus compartido"] -. metrics .-> auth
    prometheus -. metrics .-> gateway
    prometheus -. metrics .-> cliente
    prometheus -. metrics .-> ubigeo
    prometheus -. metrics .-> catalogo
    prometheus -. metrics .-> orden
    prometheus -. metrics .-> pago

    loki["External System: Loki compartido"] -. logs .-> auth
    loki -. logs .-> gateway
    loki -. logs .-> cliente
    loki -. logs .-> ubigeo
    loki -. logs .-> catalogo
    loki -. logs .-> orden
    loki -. logs .-> pago

    grafana["External System: Grafana compartido"] --> prometheus
    grafana --> loki
```

Este nivel ya puede mostrar tecnologia y responsabilidades internas: Angular, Gateway, microservicios, bases de configuracion, servicios de soporte y sistemas externos consumidos por los contenedores.

### Vista Dinamica: Cliente y Ubigeo

```mermaid
flowchart LR
    usuario["Person: Usuario institucional"] --> angular["Container: Angular App"]
    angular --> gateway["Container: Gateway"]

    gateway --> cliente["Container: cliente-ms"]
    gateway --> ubigeo["Container: ubigeo-ms"]

    cliente -- Feign + Circuit Breaker --> ubigeo

    config["Container: Config Server"] --> configrepo[(Data Store: config-repo)]
    cliente -. config .-> config
    ubigeo -. config .-> config
    gateway -. config .-> config

    eureka["Container: Eureka"] -. registro .- cliente
    eureka -. registro .- ubigeo
    eureka -. registro .- gateway
```

### Vista Dinamica: Orden y Pago

```mermaid
flowchart LR
    gateway["Container: Gateway"] --> orden["Container: orden-ms"]
    gateway --> pago["Container: pago-ms"]

    orden -- publica orden.creada --> kafka["External System: Kafka empresarial compartido"]
    kafka -- consume orden.creada --> pago
    pago -- autoriza/captura pago --> pasarela["External System: Pasarela de pago (Niubiz/Culqi)"]
    pago -- publica pago.validado --> kafka

    config["Container: Config Server"] --> configrepo[(Data Store: config-repo)]
    orden -. config .-> config
    pago -. config .-> config
    gateway -. config .-> config

    eureka["Container: Eureka"] -. registro .- orden
    eureka -. registro .- pago
    eureka -. registro .- gateway
```

### C4 Nivel 3: Container - orden-ms y pago-ms

Componentes principales del flujo de orden y pago.

```mermaid
flowchart LR
    subgraph orden["Container: orden-ms"]
        ordenController["Component: OrdenController"]
        ordenService["Component: OrdenService"]
        clienteClient["Component: ClienteClient Feign"]
        catalogoClient["Component: CatalogoClient Feign"]
        ordenProducer["Component: OrdenEventProducer"]
        ordenRepo["Component: OrdenRepository"]
        ordenDb[(Data Store: pagatu_orden_db)]

        ordenController --> ordenService
        ordenService --> clienteClient
        ordenService --> catalogoClient
        ordenService --> ordenRepo
        ordenService --> ordenProducer
        ordenRepo --> ordenDb
    end

    subgraph pago["Container: pago-ms"]
        pagoConsumer["Component: OrdenCreadaConsumer"]
        pagoService["Component: PagoService"]
        pasarelaClient["Component: PasarelaPagoClient"]
        pagoProducer["Component: PagoEventProducer"]
        pagoRepo["Component: PagoRepository"]
        pagoDb[(Data Store: pagatu_pago_db)]

        pagoConsumer --> pagoService
        pagoService --> pasarelaClient
        pagoService --> pagoRepo
        pagoService --> pagoProducer
        pagoRepo --> pagoDb
    end

    clienteClient --> cliente["Container: cliente-ms"]
    catalogoClient --> catalogo["Container: catalogo-ms"]
    ordenProducer --> kafka["External System: Kafka empresarial compartido"]
    kafka --> pagoConsumer
    pagoProducer --> kafka
    pasarelaClient --> pasarela["External System: Pasarela de pago (Niubiz/Culqi)"]
```

Este nivel baja el zoom dentro de dos contenedores concretos del Release 1. `orden-ms` conserva la decision sincrona por Feign para validar cliente y catalogo antes de crear la orden. `pago-ms` reacciona por Kafka a `orden.creada`, encapsula la pasarela externa y publica el resultado `pago.validado`.

### C4 Nivel 4: Component - ClienteService

Codigo de ejemplo en `cliente-ms`.

```mermaid
classDiagram
    class SecurityConfig {
        +securityFilterChain(HttpSecurity) SecurityFilterChain
        +jwtAuthenticationConverter() Converter
    }

    class SecurityFilterChain {
        +valida JWT
        +valida roles por endpoint
    }

    class JwtRoleConverter {
        +convert(Jwt) Collection~GrantedAuthority~
    }

    class ClienteController {
        +crearCliente(CrearClienteRequest) ClienteResponse
        +buscarPorDocumento(String) ClienteResponse
        +validarCliente(Long) ClienteValidadoResponse
    }

    class ClienteService {
        +crearCliente(CrearClienteRequest) ClienteResponse
        +buscarPorDocumento(String) ClienteResponse
        +validarCliente(Long) ClienteValidadoResponse
    }

    class ClienteServiceImpl {
        -ClienteRepository clienteRepository
        -UbigeoClient ubigeoClient
        -ClienteMapper clienteMapper
        +crearCliente(CrearClienteRequest) ClienteResponse
    }

    class UbigeoClient {
        +buscarDistrito(String) UbigeoResponse
    }

    class ClienteRepository {
        +save(Cliente) Cliente
        +findByDocumento(String) Optional~Cliente~
        +existsById(Long) boolean
    }

    class ClienteMapper {
        +toEntity(CrearClienteRequest) Cliente
        +toResponse(Cliente) ClienteResponse
    }

    class Cliente {
        -Long id
        -String documento
        -String nombres
        -String apellidos
        -String ubigeoNacimiento
        -Boolean activo
    }

    SecurityConfig --> SecurityFilterChain
    SecurityConfig --> JwtRoleConverter
    SecurityFilterChain ..> ClienteController : protege
    ClienteController --> ClienteService
    ClienteService <|.. ClienteServiceImpl
    ClienteServiceImpl ..> ClienteRepository
    ClienteServiceImpl ..> UbigeoClient
    ClienteServiceImpl ..> ClienteMapper
    ClienteRepository --> Cliente
```

### C4 Nivel 4: Component - OrdenService

Codigo de ejemplo en `orden-ms`.

```mermaid
classDiagram
    class SecurityConfig {
        +securityFilterChain(HttpSecurity) SecurityFilterChain
        +jwtAuthenticationConverter() Converter
    }

    class SecurityFilterChain {
        +valida JWT
        +valida roles por endpoint
    }

    class JwtRoleConverter {
        +convert(Jwt) Collection~GrantedAuthority~
    }

    class OrdenController {
        +crearOrden(CrearOrdenRequest) OrdenResponse
        +buscarPorId(Long) OrdenResponse
        +listarPorCliente(Long) List~OrdenResponse~
    }

    class OrdenService {
        +crearOrden(CrearOrdenRequest) OrdenResponse
        +buscarPorId(Long) OrdenResponse
        +listarPorCliente(Long) List~OrdenResponse~
    }

    class OrdenServiceImpl {
        -ClienteClient clienteClient
        -CatalogoClient catalogoClient
        -OrdenRepository ordenRepository
        -OrdenEventProducer ordenEventProducer
        +crearOrden(CrearOrdenRequest) OrdenResponse
    }

    class ClienteClient {
        +validarCliente(Long) ClienteValidadoResponse
    }

    class CatalogoClient {
        +validarItem(Long) CatalogoItemResponse
    }

    class OrdenRepository {
        +save(Orden) Orden
        +findByClienteId(Long) List~Orden~
    }

    class OrdenEventProducer {
        +publicarOrdenCreada(OrdenCreadaEvent) void
    }

    class Orden {
        -Long id
        -Long clienteId
        -BigDecimal total
        -EstadoOrden estado
    }

    SecurityConfig --> SecurityFilterChain
    SecurityConfig --> JwtRoleConverter
    SecurityFilterChain ..> OrdenController : protege
    OrdenController --> OrdenService
    OrdenService <|.. OrdenServiceImpl
    OrdenServiceImpl ..> ClienteClient
    OrdenServiceImpl ..> CatalogoClient
    OrdenServiceImpl ..> OrdenRepository
    OrdenServiceImpl ..> OrdenEventProducer
    OrdenRepository --> Orden
```

Este nivel se usa solo como ejemplo didactico. En C4, el nivel de codigo no deberia convertirse en un diagrama de todas las clases del proyecto; sirve para explicar una parte puntual cuando aporta claridad.

### C4 Nivel 4: Component - Angular Ordenes

Codigo de ejemplo del frontend para el flujo de ordenes.

```mermaid
classDiagram
    class OrdenPageComponent {
        +ngOnInit() void
        +crearOrden() void
        +cargarOrdenesCliente() void
        +verDetalle(Long) void
    }

    class OrdenFormComponent {
        +form: FormGroup
        +agregarItem() void
        +quitarItem(index) void
        +emitirOrden() void
    }

    class OrdenDetalleComponent {
        +orden: OrdenResponse
        +mostrarEstadoPago() void
    }

    class OrdenApiService {
        +crearOrden(CrearOrdenRequest) Observable~OrdenResponse~
        +buscarPorId(Long) Observable~OrdenResponse~
        +listarPorCliente(Long) Observable~OrdenResponse[]~
    }

    class CatalogoApiService {
        +listarItemsActivos() Observable~CatalogoItemResponse[]~
        +buscarItem(Long) Observable~CatalogoItemResponse~
    }

    class ClienteSessionService {
        +obtenerClienteActual() ClienteResponse
        +tieneSesionActiva() boolean
    }

    class AuthGuard {
        +canActivate() boolean
    }

    class AuthApiService {
        +login(LoginRequest) Observable~AuthResponse~
        +refreshToken() Observable~AuthResponse~
        +logout() void
    }

    class TokenStorageService {
        +guardarToken(String) void
        +obtenerToken() String
        +limpiarSesion() void
    }

    class AuthInterceptor {
        +intercept(req, next) Observable~HttpEvent~
    }

    class TraceInterceptor {
        +intercept(req, next) Observable~HttpEvent~
    }

    class GatewayApi {
        +/api/auth/**
        +/api/ordenes/**
        +/api/catalogo/**
    }

    AuthGuard --> ClienteSessionService
    AuthGuard --> TokenStorageService
    AuthApiService --> GatewayApi
    AuthApiService --> TokenStorageService
    AuthInterceptor --> TokenStorageService
    OrdenPageComponent --> OrdenFormComponent
    OrdenPageComponent --> OrdenDetalleComponent
    OrdenPageComponent --> OrdenApiService
    OrdenFormComponent --> CatalogoApiService
    OrdenFormComponent --> ClienteSessionService
    OrdenApiService --> AuthInterceptor
    OrdenApiService --> TraceInterceptor
    CatalogoApiService --> AuthInterceptor
    CatalogoApiService --> TraceInterceptor
    AuthInterceptor --> GatewayApi
    TraceInterceptor --> GatewayApi
```

Angular se muestra aparte porque vive en otro proyecto/contenedor. Este diagrama explica la pantalla de ordenes, sus componentes, servicios HTTP e interceptores, mientras el diagrama de `orden-ms` queda enfocado en el backend.

### C4 Nivel 4: Component - PagoService

Codigo de ejemplo en `pago-ms`.

```mermaid
classDiagram
    class SecurityConfig {
        +securityFilterChain(HttpSecurity) SecurityFilterChain
        +jwtAuthenticationConverter() Converter
    }

    class SecurityFilterChain {
        +valida JWT
        +valida roles por endpoint
    }

    class JwtRoleConverter {
        +convert(Jwt) Collection~GrantedAuthority~
    }

    class PagoController {
        +buscarPorOrden(Long) PagoResponse
        +validarPago(Long) PagoResponse
        +rechazarPago(Long, String) PagoResponse
    }

    class OrdenCreadaConsumer {
        +consumir(OrdenCreadaEvent) void
    }

    class PagoService {
        +registrarDesdeOrden(OrdenCreadaEvent) PagoResponse
        +validarPago(Long) PagoResponse
        +rechazarPago(Long, String) PagoResponse
    }

    class PagoServiceImpl {
        -PagoRepository pagoRepository
        -PasarelaPagoClient pasarelaPagoClient
        -PagoEventProducer pagoEventProducer
        -PagoMapper pagoMapper
        +registrarDesdeOrden(OrdenCreadaEvent) PagoResponse
    }

    class PasarelaPagoClient {
        +autorizar(Pago) RespuestaPasarela
        +capturar(String) RespuestaPasarela
    }

    class PagoEventProducer {
        +publicarPagoValidado(PagoValidadoEvent) void
        +publicarPagoRechazado(PagoRechazadoEvent) void
    }

    class PagoRepository {
        +save(Pago) Pago
        +findByOrdenId(Long) Optional~Pago~
        +findById(Long) Optional~Pago~
    }

    class PagoMapper {
        +toResponse(Pago) PagoResponse
    }

    class Pago {
        -Long id
        -Long ordenId
        -BigDecimal monto
        -EstadoPago estado
        -String codigoOperacion
    }

    SecurityConfig --> SecurityFilterChain
    SecurityConfig --> JwtRoleConverter
    SecurityFilterChain ..> PagoController : protege
    PagoController --> PagoService
    OrdenCreadaConsumer --> PagoService
    PagoService <|.. PagoServiceImpl
    PagoServiceImpl ..> PagoRepository
    PagoServiceImpl ..> PasarelaPagoClient
    PagoServiceImpl ..> PagoEventProducer
    PagoServiceImpl ..> PagoMapper
    PagoRepository --> Pago
```

Estos ejemplos de codigo muestran el estilo esperado dentro de cada microservicio: el acceso HTTP se valida antes del controller con `SecurityConfig`, `SecurityFilterChain` y conversion de roles JWT; el controller depende del contrato `Service`; la clase `Impl` encapsula la orquestacion interna y desde alli se usan repositories, mappers, clients Feign y producers/consumers Kafka cuando correspondan. En consumidores Kafka, la autorizacion no entra por endpoint HTTP, pero el servicio igual conserva trazabilidad y validaciones de negocio.

### C4: Despliegue por Ambientes

```mermaid
flowchart TB
    subgraph dev[DEV local]
        devjava[MS con Java 17 local]
        devmscompose[docker-compose-dev.yml del MS]
        devshared[Infra compartida: Config + Eureka + Gateway]
        devmessaging[Mensajeria compartida: Kafka local]
        devobs[Observabilidad compartida: Prometheus + Loki + Grafana]
        devmysql[(MySQL por MS en Docker)]
        devmscompose --> devmysql
        devjava --> devshared
        devjava --> devmessaging
        devjava --> devobs
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
        k8sinfra[Config + Eureka + Gateway]
        k8sdeploy[Deployments de MS]
        k8ssvc[Services ClusterIP por MS]
        k8sdb[(MySQL por MS con StatefulSet/PVC)]
        k8splatform[Kafka y observabilidad de laboratorio]
        k8sing[Gateway o Ingress local]
        k8sing --> k8ssvc
        k8ssvc --> k8sdeploy
        k8sdeploy --> k8sinfra
        k8sdeploy --> k8sdb
        k8sdeploy --> k8splatform
    end

    subgraph cloud[Nube / Produccion real]
        cloudk8s[Kubernetes administrado para MS]
        registry[Container Registry]
        manageddb[(Base de datos administrada)]
        managedkafka[(Kafka corporativo o administrado)]
        managedobs[Observabilidad corporativa o cloud]
        cloudinfra[Config/Eureka/Gateway o equivalentes cloud]
        ingress[Ingress / Load Balancer]
        registry --> cloudk8s
        ingress --> cloudk8s
        cloudk8s --> cloudinfra
        cloudk8s --> manageddb
        cloudk8s --> managedkafka
        cloudk8s --> managedobs
    end

    dev --> compose --> k8slocal --> cloud
```

DEV local y PROD local representan el mismo sistema corriendo en paralelo, pero con distinto modo de ejecucion. En DEV local, los microservicios y la infraestructura (`config`, `eureka`, `gateway`) corren con Java 17 en la maquina del desarrollador para facilitar cambios rapidos; Docker se usa para dependencias como MySQL por MS, Kafka y observabilidad compartida. En PROD local, los mismos componentes se validan como imagenes y contenedores: la infraestructura propia se levanta con `infra/docker-compose.yml`, Kafka y observabilidad con `platform/`, y cada microservicio con su propio `<ms>/docker-compose.yml`. En Kubernetes local se usan manifiestos `k8s-local/`: los MS van como `Deployment`, los servicios internos como `ClusterIP`, MySQL puede ir como `StatefulSet` con PVC y el acceso externo pasa por Gateway o Ingress. En nube se usan imagenes desde registry, manifiestos `k8s/` para los MS, bases de datos administradas, Kafka corporativo/administrado y observabilidad de plataforma.

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
