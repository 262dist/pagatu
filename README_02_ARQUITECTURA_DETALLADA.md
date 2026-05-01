# Arquitectura Detallada de Pagatu

Este documento contiene las vistas tecnicas de mayor detalle de Pagatu. Complementa a [README_01_ACERCA_DEL_PROYECTO.md](README_01_ACERCA_DEL_PROYECTO.md), que conserva la vision general, C4 nivel 1, C4 nivel 2, despliegue por ambientes y aspectos transversales del proyecto.

## Vistas Dinamicas

### Autenticacion y Acceso

```mermaid
flowchart LR
    usuario["Person: Usuario institucional"] --> angular["Container: Angular App"]
    angular --> gateway["Container: Gateway"]
    gateway --> auth["Container: auth-ms"]
    auth --> authDb[(Data Store: pagatu_auth_db)]
    auth -- emite token --> angular

    angular -- request con JWT --> gateway
    gateway -- valida token en borde --> ms["Container: Microservicios"]
    ms -- valida JWT y roles --> recurso["Reglas de negocio protegidas"]
    ms -- usa subject/usuarioId del JWT --> perfil["Perfil de cliente cuando aplica"]

    config["Container: Config Server"] --> configrepo[(Data Store: config-repo)]
    auth -. config .-> config
    gateway -. config .-> config
    ms -. config .-> config

    eureka["Container: Eureka"] -. registro .- auth
    eureka -. registro .- gateway
    eureka -. registro .- ms
```

Este flujo muestra la separacion de responsabilidades: `auth-ms` emite el token inicial con identificador de usuario y roles; Gateway valida en borde; cada microservicio vuelve a validar JWT y roles antes de ejecutar reglas de negocio. Cuando el flujo requiere datos de cliente, `cliente-ms` usa el `subject` o `usuarioId` del JWT como referencia, sin consultar directamente a `auth-ms`.

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

### Pago y Orden

```mermaid
flowchart LR
    gateway["Container: Gateway"] --> orden["Container: orden-ms"]
    gateway --> pago["Container: pago-ms"]

    orden -- publica orden.creada / orden.cancelada por falta de pago --> kafka["External System: Kafka empresarial compartido"]
    kafka -- consume orden.creada --> pago
    pago -- autoriza/captura pago --> pasarela["External System: Pasarela de pago (Niubiz/Culqi)"]
    pago -- publica pago.validado / pago.rechazado --> kafka
    kafka -. consume eventos de orden y pago .-> notificacion["Container futuro: notificacion-ms"]

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

### Estados de Orden

```mermaid
stateDiagram-v2
    [*] --> BORRADOR

    BORRADOR --> [*]: cliente no continua / sin evento
    BORRADOR --> VALIDANDO: crear orden
    VALIDANDO --> RECHAZADA: cliente/catalogo invalido
    VALIDANDO --> CREADA: validaciones correctas

    CREADA --> PENDIENTE_PAGO: publica orden.creada
    PENDIENTE_PAGO --> CANCELADA: falta de pago / publica orden.cancelada

    CANCELADA --> [*]
    RECHAZADA --> [*]
```

`orden-ms` cambia de estado por decisiones propias del flujo de orden. Si el cliente no continua antes de confirmar, el flujo termina sin publicar eventos. Las validaciones con `cliente-ms` y `catalogo-ms` ocurren antes de crear la orden. Luego publica `orden.creada`. Si una orden ya creada queda sin pago hasta su vencimiento, `orden-ms` la marca como cancelada por falta de pago y publica `orden.cancelada`. No consume `pago.validado` ni `pago.rechazado` en Release 1. El resultado del pago vive en `pago-ms`; otros servicios como notificaciones pueden reaccionar a los eventos de orden y pago.

### Estados de Pago

```mermaid
stateDiagram-v2
    [*] --> RECIBIDO

    RECIBIDO --> REGISTRADO: consume orden.creada
    REGISTRADO --> AUTORIZANDO: envia a pasarela
    AUTORIZANDO --> VALIDADO: pasarela aprueba
    AUTORIZANDO --> RECHAZADO: pasarela rechaza
    AUTORIZANDO --> ERROR_PASARELA: timeout/error tecnico

    ERROR_PASARELA --> AUTORIZANDO: reintento controlado
    ERROR_PASARELA --> RECHAZADO: agotamiento de reintentos

    VALIDADO --> NOTIFICADO: publica pago.validado
    RECHAZADO --> NOTIFICADO: publica pago.rechazado
    NOTIFICADO --> [*]
```

`pago-ms` nace a partir del evento `orden.creada`. Registra el intento de pago, llama a la pasarela externa y publica el resultado. El estado `ERROR_PASARELA` permite diferenciar un rechazo de negocio de un problema tecnico o de comunicacion.

En Release 1, `pago-ms` consume `orden.creada` y publica eventos de pago. `orden-ms` publica eventos de orden y no consume eventos de pago. En Release 2, `notificacion-ms` podra consumir `orden.cancelada`, `pago.validado` y `pago.rechazado` para avisar al usuario.

## C4 Nivel 3

### Container - gateway, eureka y config

Componentes principales de la infraestructura propia de Pagatu.

```mermaid
flowchart LR
    subgraph gateway["Container: gateway"]
        routeLocator["Component: RouteLocator"]
        authFilter["Component: AuthFilter"]
        traceFilter["Component: CorrelationIdFilter"]
        lbClient["Component: Spring Cloud LoadBalancer"]
        gatewayDiscoveryClient["Component: EurekaClient"]
        gatewayConfigClient["Component: ConfigClient"]

        routeLocator --> authFilter
        authFilter --> traceFilter
        traceFilter --> lbClient
        lbClient -. elige instancia .-> gatewayDiscoveryClient
    end

    subgraph eureka["Container: eureka"]
        registry["Component: ServiceRegistry"]
        eurekaConfigClient["Component: ConfigClient"]
    end

    subgraph config["Container: config"]
        configController["Component: ConfigController"]
        nativeRepo["Component: NativeEnvironmentRepository"]
        configRepo[(Data Store: config-repo)]

        configController --> nativeRepo
        nativeRepo --> configRepo
    end

    subgraph ms["Container: Microservicios"]
        serviceApp["Component: Spring Boot App"]
        securityConfig["Component: SecurityConfig"]
        securityChain["Component: SecurityFilterChain"]
        roleConverter["Component: JwtRoleConverter"]
        eurekaClient["Component: EurekaClient"]
        configClient["Component: ConfigClient"]

        serviceApp --> securityConfig
        serviceApp --> eurekaClient
        serviceApp --> configClient
        securityConfig --> securityChain
        securityConfig --> roleConverter
    end

    gatewayDiscoveryClient -. obtiene instancias registradas .-> registry
    gatewayDiscoveryClient -. registro .-> registry
    authFilter -. valida token en borde .-> serviceApp
    securityChain -. valida autenticacion y autorizacion .-> serviceApp
    configClient -. solicita config .-> configController
    eurekaClient -. registro .-> registry
    gatewayConfigClient -. solicita config .-> configController
    eurekaConfigClient -. solicita config .-> configController
```

Este nivel muestra la infraestructura propia del proyecto. `config` publica configuracion desde `config-repo`; `gateway`, `eureka` y los microservicios leen su configuracion como Config Client. `eureka` mantiene el registro de servicios; los microservicios y el Gateway se registran con `EurekaClient`. Gateway usa `EurekaClient` para obtener instancias registradas y usa Spring Cloud LoadBalancer para elegir una instancia. La seguridad se valida en dos capas: Gateway aplica una primera validacion de borde y cada microservicio conserva su propia validacion de autenticacion, autorizacion y roles.

Resumen de seguridad:

```text
Gateway:
- Valida token en borde.
- Enruta y balancea.

Microservicios:
- Validan JWT y roles otra vez.
- Protegen reglas de negocio.

Config Server:
- Seguridad interna/operativa.
- Protege configuracion.

Eureka:
- Seguridad interna/operativa.
- Protege registro y discovery.
```

### Container - auth-ms

Componentes principales del servicio de autenticacion inicial.

```mermaid
flowchart LR
    subgraph auth["Container: auth-ms"]
        authController["Component: AuthController"]
        usuarioController["Component: UsuarioController"]
        authService["Component: AuthService"]
        usuarioService["Component: UsuarioService"]
        tokenService["Component: TokenService"]
        passwordEncoder["Component: PasswordEncoder"]
        usuarioRepo["Component: UsuarioRepository"]
        rolRepo["Component: RolRepository"]
        authDb[(Data Store: pagatu_auth_db)]

        authController --> authService
        usuarioController --> usuarioService
        authService --> usuarioRepo
        authService --> tokenService
        authService --> passwordEncoder
        usuarioService --> usuarioRepo
        usuarioService --> rolRepo
        usuarioRepo --> authDb
        rolRepo --> authDb
    end

    gateway["Container: gateway"] --> authController
    tokenService -. emite JWT con usuarioId y roles .-> gateway
```

`auth-ms` se mantiene como autenticacion inicial de Release 1. Es duenio de usuarios, credenciales y roles. Mas adelante puede reemplazarse o integrarse con Keycloak, pero los demas servicios seguiran consumiendo tokens y roles bajo la misma idea arquitectonica.

### Container - cliente-ms y ubigeo-ms

Componentes principales del flujo de cliente y ubigeo.

```mermaid
flowchart LR
    subgraph cliente["Container: cliente-ms"]
        clienteController["Component: ClienteController"]
        clienteService["Component: ClienteService"]
        usuarioContext["Component: UsuarioAutenticadoProvider"]
        ubigeoClient["Component: UbigeoClient Feign"]
        clienteRepo["Component: ClienteRepository"]
        clienteMapper["Component: ClienteMapper"]
        clienteDb[(Data Store: pagatu_cliente_db)]

        clienteController --> usuarioContext
        clienteController --> clienteService
        usuarioContext --> clienteService
        clienteService --> ubigeoClient
        clienteService --> clienteRepo
        clienteService --> clienteMapper
        clienteRepo --> clienteDb
    end

    subgraph ubigeo["Container: ubigeo-ms"]
        ubigeoController["Component: UbigeoController"]
        ubigeoService["Component: UbigeoService"]
        ubigeoRepo["Component: UbigeoRepository"]
        ubigeoMapper["Component: UbigeoMapper"]
        ubigeoDb[(Data Store: pagatu_ubigeo_db)]

        ubigeoController --> ubigeoService
        ubigeoService --> ubigeoRepo
        ubigeoService --> ubigeoMapper
        ubigeoRepo --> ubigeoDb
    end

    ubigeoClient --> ubigeoController
```

Este nivel baja el zoom dentro de dos contenedores concretos del Release 1. `cliente-ms` gestiona los datos del cliente y consulta a `ubigeo-ms` por Feign cuando necesita completar o validar datos geograficos como nacimiento, residencia o direccion. La relacion con el usuario autenticado se toma desde el JWT mediante `UsuarioAutenticadoProvider`; no se consulta a `auth-ms` por Feign.

### Container - orden-ms y catalogo-ms

Componentes principales del flujo de orden y catalogo.

```mermaid
flowchart LR
    subgraph orden["Container: orden-ms"]
        ordenController["Component: OrdenController"]
        ordenService["Component: OrdenService"]
        catalogoClient["Component: CatalogoClient Feign"]
        ordenRepo["Component: OrdenRepository"]
        ordenMapper["Component: OrdenMapper"]
        ordenDb[(Data Store: pagatu_orden_db)]

        ordenController --> ordenService
        ordenService --> catalogoClient
        ordenService --> ordenRepo
        ordenService --> ordenMapper
        ordenRepo --> ordenDb
    end

    subgraph catalogo["Container: catalogo-ms"]
        catalogoController["Component: CatalogoController"]
        catalogoService["Component: CatalogoService"]
        productoRepo["Component: ProductoRepository"]
        precioRepo["Component: PrecioRepository"]
        catalogoMapper["Component: CatalogoMapper"]
        catalogoDb[(Data Store: pagatu_catalogo_db)]

        catalogoController --> catalogoService
        catalogoService --> productoRepo
        catalogoService --> precioRepo
        catalogoService --> catalogoMapper
        productoRepo --> catalogoDb
        precioRepo --> catalogoDb
    end

    catalogoClient --> catalogoController
```

Este nivel muestra la validacion sincrona de items antes de crear una orden. `orden-ms` consulta a `catalogo-ms` por Feign para validar productos, conceptos, familias, categorias, tipos, precios y estado activo.

### Container - pago-ms y orden-ms

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
    pagoProducer -- pago.validado / pago.rechazado --> kafka
    pasarelaClient --> pasarela["External System: Pasarela de pago (Niubiz/Culqi)"]

    classDef external fill:#fff3cd,stroke:#d97706,stroke-width:2px,color:#111827
    class pasarela external
```

Este nivel baja el zoom dentro de dos contenedores concretos del Release 1. `orden-ms` conserva la decision sincrona por Feign para validar cliente y catalogo antes de crear la orden, y publica eventos propios de orden como `orden.creada` y `orden.cancelada` cuando corresponde por falta de pago. `pago-ms` consume `orden.creada`, encapsula la pasarela externa y publica `pago.validado` o `pago.rechazado` para consumidores posteriores, como `notificacion-ms` en Release 2.

## C4 Nivel 4

### Component - AuthService

Codigo de ejemplo en `auth-ms`.

```mermaid
classDiagram
    class AuthController {
        +login(LoginRequest) AuthResponse
        +refresh(RefreshTokenRequest) AuthResponse
        +logout() void
    }

    class UsuarioController {
        +crearUsuario(CrearUsuarioRequest) UsuarioResponse
        +asignarRol(Long, String) UsuarioResponse
    }

    class AuthService {
        +login(LoginRequest) AuthResponse
        +refresh(RefreshTokenRequest) AuthResponse
    }

    class AuthServiceImpl {
        -UsuarioRepository usuarioRepository
        -TokenService tokenService
        -PasswordEncoder passwordEncoder
        +login(LoginRequest) AuthResponse
    }

    class UsuarioService {
        +crearUsuario(CrearUsuarioRequest) UsuarioResponse
        +asignarRol(Long, String) UsuarioResponse
    }

    class TokenService {
        +generarToken(Usuario) String
        +validarToken(String) boolean
        +obtenerUsuarioId(String) Long
        +obtenerRoles(String) List~String~
    }

    class UsuarioRepository {
        +findByUsername(String) Optional~Usuario~
        +save(Usuario) Usuario
    }

    class RolRepository {
        +findByNombre(String) Optional~Rol~
    }

    class Usuario {
        -Long id
        -String username
        -String passwordHash
        -Boolean activo
    }

    class Rol {
        -Long id
        -String nombre
    }

    AuthController --> AuthService
    UsuarioController --> UsuarioService
    AuthService <|.. AuthServiceImpl
    AuthServiceImpl ..> UsuarioRepository
    AuthServiceImpl ..> TokenService
    UsuarioService ..> UsuarioRepository
    UsuarioService ..> RolRepository
    UsuarioRepository --> Usuario
    RolRepository --> Rol
```

Este ejemplo representa la autenticacion inicial del curso. No busca reemplazar a Keycloak en escenarios empresariales completos; sirve para entender tokens, roles y control de acceso antes de integrar un proveedor de identidad.

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
        +obtenerMiPerfil() ClienteResponse
        +vincularUsuarioActual(Long) ClienteResponse
        +buscarPorDocumento(String) ClienteResponse
        +validarCliente(Long) ClienteValidadoResponse
    }

    class ClienteService {
        +crearCliente(CrearClienteRequest) ClienteResponse
        +obtenerMiPerfil(Long usuarioId) ClienteResponse
        +vincularUsuario(Long usuarioId, Long clienteId) ClienteResponse
        +buscarPorDocumento(String) ClienteResponse
        +validarCliente(Long) ClienteValidadoResponse
    }

    class ClienteServiceImpl {
        -ClienteRepository clienteRepository
        -UsuarioAutenticadoProvider usuarioAutenticadoProvider
        -UbigeoClient ubigeoClient
        -ClienteMapper clienteMapper
        +crearCliente(CrearClienteRequest) ClienteResponse
        +obtenerMiPerfil(Long usuarioId) ClienteResponse
    }

    class UsuarioAutenticadoProvider {
        +obtenerUsuarioId() Long
        +obtenerRoles() List~String~
    }

    class UbigeoClient {
        +buscarDistrito(String) UbigeoResponse
    }

    class ClienteRepository {
        +save(Cliente) Cliente
        +findByDocumento(String) Optional~Cliente~
        +findByUsuarioIdAuth(Long) Optional~Cliente~
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
        -Long usuarioIdAuth
        -String ubigeoNacimiento
        -Boolean activo
    }

    SecurityConfig --> SecurityFilterChain
    SecurityConfig --> JwtRoleConverter
    SecurityFilterChain ..> ClienteController : protege
    SecurityFilterChain ..> UsuarioAutenticadoProvider : expone usuario actual
    ClienteController --> ClienteService
    ClienteController --> UsuarioAutenticadoProvider
    ClienteService <|.. ClienteServiceImpl
    ClienteServiceImpl ..> ClienteRepository
    ClienteServiceImpl ..> UsuarioAutenticadoProvider
    ClienteServiceImpl ..> UbigeoClient
    ClienteServiceImpl ..> ClienteMapper
    ClienteRepository --> Cliente
```

La relacion entre usuario y cliente se muestra en `cliente-ms`, no como llamada directa a `auth-ms`. `auth-ms` es duenio de la identidad; `cliente-ms` solo guarda `usuarioIdAuth` como referencia al usuario autenticado y lo obtiene desde el JWT mediante `UsuarioAutenticadoProvider`. Asi se puede resolver el perfil del cliente actual sin acoplar ambos microservicios.

### Component - UbigeoService

Codigo de ejemplo en `ubigeo-ms`.

```mermaid
classDiagram
    class UbigeoController {
        +listarPaises() List~PaisResponse~
        +listarDepartamentos(String) List~DepartamentoResponse~
        +listarProvincias(String) List~ProvinciaResponse~
        +listarDistritos(String) List~DistritoResponse~
        +buscarDistrito(String) UbigeoResponse
    }

    class UbigeoService {
        +listarPaises() List~PaisResponse~
        +buscarDistrito(String) UbigeoResponse
    }

    class UbigeoServiceImpl {
        -PaisRepository paisRepository
        -DepartamentoRepository departamentoRepository
        -ProvinciaRepository provinciaRepository
        -DistritoRepository distritoRepository
        -UbigeoMapper ubigeoMapper
        +buscarDistrito(String) UbigeoResponse
    }

    class DistritoRepository {
        +findByCodigo(String) Optional~Distrito~
        +findByProvinciaCodigo(String) List~Distrito~
    }

    class UbigeoMapper {
        +toResponse(Distrito) UbigeoResponse
    }

    class Distrito {
        -Long id
        -String codigo
        -String nombre
    }

    UbigeoController --> UbigeoService
    UbigeoService <|.. UbigeoServiceImpl
    UbigeoServiceImpl ..> DistritoRepository
    UbigeoServiceImpl ..> UbigeoMapper
    DistritoRepository --> Distrito
```

`ubigeo-ms` queda como servicio sincrono estable y de consulta publica. Su responsabilidad es exponer datos territoriales conocidos, no proteger reglas de negocio ni evolucionar a mensajeria de eventos.

### Component - CatalogoService

Codigo de ejemplo en `catalogo-ms`.

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

    class ProductoController {
        +listarActivos() List~ProductoResponse~
        +buscarProducto(Long) ProductoResponse
        +crearProducto(CrearProductoRequest) CatalogoItemResponse
        +actualizarProducto(Long, ActualizarProductoRequest) CatalogoItemResponse
        +desactivar(Long) void
    }

    class CategoriaController {
        +listarCategorias() List~CategoriaResponse~
        +crearCategoria(CrearCategoriaRequest) CategoriaResponse
        +actualizarCategoria(Long, ActualizarCategoriaRequest) CategoriaResponse
    }

    class FamiliaController {
        +listarFamilias() List~FamiliaResponse~
        +crearFamilia(CrearFamiliaRequest) FamiliaResponse
    }

    class ConceptoController {
        +listarConceptos() List~ConceptoResponse~
        +buscarConcepto(Long) ConceptoResponse
    }

    class PrecioController {
        +buscarPrecioVigente(Long) PrecioResponse
        +registrarPrecio(CrearPrecioRequest) PrecioResponse
    }

    class CatalogoConsultaController {
        +validarItem(Long) CatalogoItemResponse
        +listarItemsActivos() List~CatalogoItemResponse~
    }

    class ProductoService {
        +crearProducto(CrearProductoRequest) CatalogoItemResponse
        +listarActivos() List~ProductoResponse~
        +desactivar(Long) void
    }

    class CategoriaService {
        +listarCategorias() List~CategoriaResponse~
        +crearCategoria(CrearCategoriaRequest) CategoriaResponse
    }

    class FamiliaService {
        +listarFamilias() List~FamiliaResponse~
        +crearFamilia(CrearFamiliaRequest) FamiliaResponse
    }

    class ConceptoService {
        +buscarConcepto(Long) ConceptoResponse
    }

    class PrecioService {
        +buscarPrecioVigente(Long) PrecioResponse
        +registrarPrecio(CrearPrecioRequest) PrecioResponse
    }

    class CatalogoConsultaService {
        +validarItem(Long) CatalogoItemResponse
    }

    class CatalogoConsultaServiceImpl {
        -ProductoRepository productoRepository
        -ConceptoRepository conceptoRepository
        -PrecioRepository precioRepository
        -CatalogoMapper catalogoMapper
        +validarItem(Long) CatalogoItemResponse
    }

    class ProductoRepository {
        +findById(Long) Optional~Producto~
        +findByActivoTrue() List~Producto~
        +save(Producto) Producto
    }

    class ConceptoRepository {
        +findById(Long) Optional~Concepto~
    }

    class CategoriaRepository {
        +findById(Long) Optional~Categoria~
        +findByActivoTrue() List~Categoria~
    }

    class FamiliaRepository {
        +findById(Long) Optional~Familia~
        +findByActivoTrue() List~Familia~
    }

    class PrecioRepository {
        +findVigenteByItemId(Long) Optional~Precio~
    }

    class CatalogoMapper {
        +toResponse(Producto, Precio) CatalogoItemResponse
    }

    class Producto {
        -Long id
        -String nombre
        -String tipo
        -Boolean activo
    }

    class Categoria {
        -Long id
        -String nombre
        -Boolean activo
    }

    class Familia {
        -Long id
        -String nombre
        -Boolean activo
    }

    class Concepto {
        -Long id
        -String nombre
        -String tipo
        -Boolean activo
    }

    class Precio {
        -Long id
        -BigDecimal monto
        -LocalDate fechaInicio
        -LocalDate fechaFin
    }

    SecurityConfig --> SecurityFilterChain
    SecurityFilterChain ..> ProductoController : protege escritura
    SecurityFilterChain ..> CategoriaController : protege escritura
    SecurityFilterChain ..> FamiliaController : protege escritura
    SecurityFilterChain ..> PrecioController : protege escritura
    ProductoController --> ProductoService
    CategoriaController --> CategoriaService
    FamiliaController --> FamiliaService
    ConceptoController --> ConceptoService
    PrecioController --> PrecioService
    CatalogoConsultaController --> CatalogoConsultaService
    ProductoService ..> ProductoRepository
    CategoriaService ..> CategoriaRepository
    FamiliaService ..> FamiliaRepository
    ConceptoService ..> ConceptoRepository
    PrecioService ..> PrecioRepository
    CatalogoConsultaService <|.. CatalogoConsultaServiceImpl
    CatalogoConsultaServiceImpl ..> ProductoRepository
    CatalogoConsultaServiceImpl ..> ConceptoRepository
    CatalogoConsultaServiceImpl ..> PrecioRepository
    CatalogoConsultaServiceImpl ..> CatalogoMapper
    ProductoRepository --> Producto
    ConceptoRepository --> Concepto
    CategoriaRepository --> Categoria
    FamiliaRepository --> Familia
    PrecioRepository --> Precio
```

`catalogo-ms` es un solo microservicio, pero no significa un solo controller. Puede exponer controladores por recurso: productos, categorias, familias, conceptos y precios. Las consultas de catalogo pueden ser publicas porque muestran informacion comercial disponible para el usuario. La edicion del catalogo si queda protegida por JWT y roles. Cuando se crea una orden, la autenticacion ocurre en `orden-ms`; luego `orden-ms` consulta `catalogo-ms` por Feign para decidir si un item puede entrar en la orden.

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

    class OrdenVencidaJob {
        +cancelarOrdenesVencidas() void
    }

    class OrdenService {
        +crearOrden(CrearOrdenRequest) OrdenResponse
        +cancelarPorFaltaPago(Long) OrdenResponse
        +buscarPorId(Long) OrdenResponse
        +listarPorCliente(Long) List~OrdenResponse~
    }

    class OrdenServiceImpl {
        -ClienteClient clienteClient
        -CatalogoClient catalogoClient
        -OrdenRepository ordenRepository
        -OrdenEventProducer ordenEventProducer
        +crearOrden(CrearOrdenRequest) OrdenResponse
        +cancelarPorFaltaPago(Long) OrdenResponse
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
        +publicarOrdenCancelada(OrdenCanceladaEvent) void
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
    OrdenVencidaJob --> OrdenService
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
