# Arquitectura Detallada de Pagatu

Este documento contiene las vistas tecnicas de mayor detalle de Pagatu. Complementa a `README_01_ACERCA_DEL_PROYECTO.md`, que conserva la vision general, C4 nivel 1, C4 nivel 2 y despliegue por ambientes.

## Vistas Dinamicas

### Cliente y Ubigeo

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

### Orden y Pago

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

    classDef external fill:#fff3cd,stroke:#d97706,stroke-width:2px,color:#111827
    class pasarela external
```

## C4 Nivel 3

### Container - orden-ms y pago-ms

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

    classDef external fill:#fff3cd,stroke:#d97706,stroke-width:2px,color:#111827
    class pasarela external
```

Este nivel baja el zoom dentro de dos contenedores concretos del Release 1. `orden-ms` conserva la decision sincrona por Feign para validar cliente y catalogo antes de crear la orden. `pago-ms` reacciona por Kafka a `orden.creada`, encapsula la pasarela externa y publica el resultado `pago.validado`.

## C4 Nivel 4

### Component - ClienteService

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

### Component - OrdenService

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

### Component - Angular Ordenes

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

### Component - PagoService

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
