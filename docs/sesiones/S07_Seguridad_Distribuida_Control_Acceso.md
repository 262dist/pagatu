# S7 - Seguridad distribuida y control de acceso

*Por: Angel Sullon Macalupu @asullom - 2026*

## 1. Introducción

Tiempo: 20 min.

### 1.1 Presentación de la sesión

Hasta S6, `pagatu-orden-ms` confía en el `idCliente` que el propio request declara — el DTO `OrdenRequest` lo trae como un campo más, igual que `metodoPago`. Nada impide que cualquiera, con Swagger o una simple petición, escriba `"idCliente": 1` y cree una orden a nombre de otra persona: el sistema nunca pregunta *quién eres*, solo confía en lo que el request dice ser. Esta sesión cierra esa puerta: aparece `pagatu-auth-ms`, un microservicio nuevo que autentica usuarios y emite un JWT firmado; `pagatu-gateway` pasa a exigir y validar ese JWT antes de dejar pasar cualquier petición (Resource Server); y `pagatu-orden-ms` deja de aceptar `idCliente` en el request — lo toma directo del JWT, que también valida por su cuenta.

### 1.2 Índice

1. Autenticación stateless con JWT.
2. Autorización basada en roles.
3. OAuth2 y OpenID Connect: el estándar detrás de un proveedor de identidad.
4. Resource Server y validación de tokens con claves públicas.
5. Observabilidad y diagnóstico.

### 1.3 Propósito de aprendizaje

Al concluir la clase, estarás en condiciones de:

- **Implementar** autenticación y autorización distribuida con JWT y Spring Security, aplicando el modelo de OAuth2 (servidor de autorización, *resource server*, claves públicas) para validar tokens en el Gateway y en los microservicios, y protegiendo rutas del sistema por rol, con evidencia real de accesos permitidos y denegados.

### 1.4 Producto de sesión

`pagatu-auth-ms` funcional — con usuarios y roles en tablas propias, login que emite un JWT firmado con clave privada y publica su clave pública, conectado a Config Server y a Eureka —; `pagatu-gateway` protegido como Resource Server, con rutas restringidas por rol (`ADMIN`/`CLIENTE`); y `pagatu-orden-ms`, también Resource Server, tomando `idCliente` del claim del JWT ya validado, no del request. `pagatu-auth-ms` es una pieza **temporal** de aprendizaje: está diseñado para que el reemplazo posterior por Keycloak cambie una propiedad de configuración, no el código del Gateway ni de los microservicios de negocio.

### 1.5 Metodología

**Tabla 1. Metodología de la sesión**

| Actividades a Realizar en el Periodo | Orientaciones generales (Orientaciones Metodológicas) | Material de estudio recomendado |
|---|---|---|
| Revisión previa individual | Confirmar que `pagatu-config`, `pagatu-eureka`, `pagatu-gateway`, `pagatu-catalogo-ms` y `pagatu-orden-ms` (S1-S6) siguen arrancando en DEV. Revisar el `OrdenRequest` actual de `pagatu-orden-ms` (S6, 3.5) y confirmar que `idCliente` hoy es un campo libre del request. Trabajo individual, antes de clase. | Evidencia individual de S6, [Alcance por microservicio y proyecto base](../proyecto-sello/alcance-microservicios.md). |
| Clase presencial | Construcción guiada de `pagatu-auth-ms` de punta a punta, protección de `pagatu-gateway` como Resource Server, y conversión de `pagatu-orden-ms` en Resource Server que toma `idCliente` del JWT. Trabajo individual, siguiendo al docente paso a paso; consulta inmediata ante un `401`/`403` inesperado. | Pasos 3.1 a 3.25 de esta guía. |
| Evaluación formativa | Revisión en clase de la matriz de accesos (3.23): login exitoso, acceso denegado sin token, acceso denegado por rol incorrecto, y `pagatu-orden-ms` creando una orden con el `idCliente` tomado del JWT. La evidencia se completa y sustenta de forma individual, fuera del aula, según los criterios mínimos de la sección 4.4. | Indicaciones de entrega (4.3), rúbrica de evaluación (4.6). |

### 1.6 Motivación de la sesión

#### 1.6.1 Caso: la orden que se creó a nombre de otra persona

Un compañero de equipo revisa `pagatu-orden-ms` con Swagger, un día antes de la sustentación de S5. Prueba el endpoint `POST /api/v1/ordenes` con el cuerpo de ejemplo que ya trae la documentación — y sin querer, sin ninguna credencial, crea una orden real con `idCliente: 1`. El sistema la acepta sin preguntar nada: ni quién hizo la petición, ni si esa persona tiene permiso de actuar en nombre del cliente `1`. `idCliente` es solo un número más dentro de un JSON, tan editable como `metodoPago`.

El problema no es que alguien haya probado el endpoint — es que el sistema nunca definió *quién puede decir que es quién*. Cualquier dato que el propio cliente declara sobre sí mismo (su identidad, su rol) no es un dato confiable: hay que verificarlo contra algo que el cliente no controla. Un JWT firmado por un servicio de confianza (`pagatu-auth-ms`) es exactamente eso — el cliente no puede fabricar uno válido sin la clave privada con la que se firma, así que cualquier claim dentro de un JWT válido (incluido `idCliente`) sí es confiable.

**Preguntas de análisis**

**Activación de conocimientos previos**

1. ¿Por qué un campo `idCliente` dentro del cuerpo de un request HTTP no es, por sí solo, una prueba de identidad?
2. Si `pagatu-gateway` ya rechaza peticiones sin token válido, ¿por qué `pagatu-orden-ms` necesita además validar el JWT por su cuenta — no le alcanza con que el Gateway ya lo validó?

**Comprensión de seguridad distribuida**

1. Con `pagatu-gateway` como único punto de acceso (S4) y como Resource Server (hoy), ¿qué pasaría si alguien intentara llamar a `pagatu-orden-ms` directo por su puerto (`8082`), sin pasar por el Gateway? Relaciónalo con 2.1 y con S4 (producción local, ningún microservicio expone puerto al host salvo el Gateway).
2. ¿Qué diferencia hay entre "autenticar" (confirmar quién eres) y "autorizar" (confirmar qué puedes hacer)? Ubica un ejemplo de cada uno en el caso de 1.6.1.

### 1.7 Ubicación en el curso

- Unidad: U2 - Sistema distribuido robusto.
- Producto del curso: Proyecto Sello: sistema distribuido de microservicios end-to-end, configurable, escalable, seguro, resiliente, consistente, observable, integrado con frontend y defendido técnicamente.
- Producto de unidad: sistema distribuido seguro, resiliente, consistente, observable e integrado con cliente frontend.
- Avance del producto en esta sesión: tercer microservicio del proyecto (`pagatu-auth-ms`), con `pagatu-gateway` protegido como Resource Server y `pagatu-orden-ms` consumiendo el JWT ya validado.

**Figura 1. Roadmap del producto de la unidad**

```mermaid
flowchart TB
    ClientePrueba["Cliente de prueba<br/>PowerShell / bash / Swagger"]
    ClienteAngular["Cliente real<br/>Angular 22 (S11)<br/>puerto 4200 (DEV)"]
    Config["pagatu-config<br/>S2 · puerto 18888 (DEV)<br/>carga de configuración"]
    Obs[("Observabilidad<br/>S10 · logs, métricas, paneles<br/>Prometheus 19090, Loki 13100<br/>Grafana 13000 (DEV)")]
    Gateway["pagatu-gateway<br/>S4 · puerto 18080 (DEV)"]
    Auth["pagatu-auth-ms<br/>S7 · JWT<br/>(temporal, luego Keycloak)"]
    Catalogo["pagatu-catalogo-ms<br/>S1 · REST + BD + health"]
    Orden["pagatu-orden-ms<br/>S6 · Feign + Circuit Breaker<br/>(S9: coordina Saga)<br/>produce: orden-eventos<br/>consume: pago-eventos"]
    Pago["pago-ms<br/>S8<br/>(S9: coordina Saga)<br/>consume: orden-eventos<br/>produce: pago-eventos"]
    Eureka["pagatu-eureka<br/>S3 · puerto 18761 (DEV)<br/>registra instancias"]
    Kafka[("Kafka<br/>S8 · puerto 19092 (DEV)<br/>topics: orden-eventos, pago-eventos")]
    Pasarela["Pasarela de pagos<br/>(externa)"]

    ClientePrueba --> Gateway
    ClienteAngular --> Gateway
    Gateway -->|"lb://pagatu-auth-ms"| Auth
    Gateway -->|"lb://pagatu-catalogo-ms"| Catalogo
    Gateway -->|"lb://pagatu-orden-ms"| Orden
    Gateway -->|"lb://pagatu-pago-ms"| Pago
    Gateway -. "descubre<br/>servicios" .-> Eureka
    Eureka -. "carga<br/>configuración" .-> Config
    Orden -->|"Feign: consulta<br/>producto"| Catalogo
    Orden -.->|"1) orden.creada"| Kafka
    Kafka -.->|"2) consume"| Pago
    Pago -.->|"3) pago.validado<br/>(S9: pago.fallido)"| Kafka
    Kafka -.->|"4) consume<br/>(S9: compensa)"| Orden
    Pago -->|"autoriza / confirma<br/>pago"| Pasarela

    classDef done fill:#e8f5e9,stroke:#2e7d32,color:#111;
    classDef today fill:#ffe08a,stroke:#9a6b00,stroke-width:2px,color:#111;
    classDef futuro fill:#f5f5f5,stroke:#9e9e9e,color:#555,stroke-dasharray: 5 5;
    classDef externo fill:#e3f2fd,stroke:#1565c0,color:#0d3c73;
    class Catalogo,Config,Eureka done;
    class Auth,Gateway,Orden today;
    class ClienteAngular,Kafka,Pago,Obs futuro;
    class Pasarela externo;
```

*Leyenda.* Este diagrama es el mismo en todas las sesiones de la unidad; solo cambia el color: verde = construido en sesiones anteriores, amarillo = se trabaja hoy, gris punteado = todavía no existe, azul = sistema externo.

`pagatu-cliente-ms` (autónomo desde S2) y su consulta a RENIEC / SUNAT no se dibujan para mantener legible el diagrama: siguen el mismo patrón de rutas, registro y configuración que los demás microservicios.

**Hoy:** aparece `pagatu-auth-ms`; `pagatu-gateway` y `pagatu-orden-ms` validan el JWT que emite, y `pagatu-orden-ms` deja de confiar en el `idCliente` del request. `pagatu-cliente-ms` se protege como trabajo autónomo (sección 4). `pagatu-auth-ms` es temporal: Keycloak lo reemplaza sin tocar el Gateway ni `pagatu-orden-ms` (2.4).

**Relaciones con la infraestructura** (no se dibujan, para mantener legible el diagrama):

- **`pagatu-config`**: `pagatu-gateway`, `pagatu-eureka` y cada microservicio cargan su configuración desde él al arrancar (S2).
- **`pagatu-eureka`**: cada microservicio se registra en él como instancia (S3), y `pagatu-gateway` lo consulta para descubrir servicios y resolver las rutas `lb://`.

**Aún no existen** (ya están agendados en el sílabo de esta unidad): Kafka y `pago-ms` (S8), Saga (S9), Observabilidad (S10) y el cliente Angular (S11).

## 2. Explica

Tiempo: 30 min.

### 2.1 Arquitectura de la sesión

**Figura 2. `pagatu-auth-ms` emite el JWT y publica su clave pública; `pagatu-gateway` y `pagatu-orden-ms` lo validan**

```mermaid
flowchart TB
    Cliente["Cliente<br/>PowerShell / bash / Swagger"]

    subgraph Paso1["1. Autenticación y emisión del token"]
        direction LR
        Auth["pagatu-auth-ms<br/>POST /api/v1/auth/login<br/>GET /.well-known/jwks.json"]
        AuthDB[("pagatu_auth_db<br/>usuarios, roles")]
        Auth --> AuthDB
    end

    subgraph Paso2["2. Validación y autorización por ruta"]
        direction LR
        GW["pagatu-gateway<br/>Resource Server<br/>valida firma + rol"]
    end

    subgraph Paso3["3. Validación dentro del servicio"]
        direction LR
        Orden["pagatu-orden-ms<br/>Resource Server<br/>lee idCliente del JWT"]
        Catalogo["pagatu-catalogo-ms"]
    end

    Cliente -->|"1. email + password"| Auth
    Auth -->|"access_token (JWT, RS256)<br/>sub, realm_access.roles, idCliente"| Cliente
    Cliente -->|"2. Authorization: Bearer JWT"| GW
    GW -->|"3a. reenvía el mismo header"| Orden
    GW -->|"3b. reenvía el mismo header"| Catalogo
    GW -. "descarga la clave<br/>pública (JWKS)" .-> Auth
    Orden -. "descarga la clave<br/>pública (JWKS)" .-> Auth

    style Paso1 fill:transparent,stroke:transparent
    style Paso2 fill:transparent,stroke:transparent
    style Paso3 fill:transparent,stroke:transparent
```

Tres pasos, tres responsabilidades que no se mezclan. `pagatu-auth-ms` (Paso 1) solo confirma credenciales, firma el JWT con su **clave privada** y publica la **clave pública** — no sabe nada de órdenes ni de productos. `pagatu-gateway` (Paso 2) es la primera línea de defensa: verifica la firma con esa clave pública y decide si el rol alcanza para la ruta, antes de que la petición llegue a ningún microservicio. `pagatu-orden-ms` (Paso 3) **también** verifica la firma por su cuenta, sin asumir que alguien más ya lo hizo: en producción local (S4) ningún microservicio publica su puerto al host salvo el Gateway, pero en DEV `pagatu-orden-ms` sigue escuchando directo en `8082` (3.22 lo demuestra), y el Producto de la unidad pide que el token se valide en cada microservicio, no solo en el borde. `pagatu-catalogo-ms` todavía no valida nada por sí mismo — extenderle esta protección es exactamente el mismo cambio de 3.19.

Ni el Gateway ni `pagatu-orden-ms` guardan ningún secreto: solo necesitan saber **dónde** descargar la clave pública. Esa es la razón por la que reemplazar `pagatu-auth-ms` por Keycloak después es un cambio de configuración y no de código (2.4).

### 2.2 Autenticación stateless con JWT

Un sistema tradicional con sesiones guarda, en el servidor, quién está autenticado — una cookie de sesión es solo una referencia a ese estado guardado. Eso no encaja con un sistema distribuido: si la sesión vive en la memoria de una instancia de `pagatu-gateway`, y el balanceador (S4) manda la siguiente petición a otra instancia, esa instancia no tiene idea de que el usuario ya se autenticó.

Un **JWT** (*JSON Web Token*) resuelve esto sin guardar nada en el servidor: es un token que lleva **dentro de sí mismo** toda la información necesaria (sus *claims* — quién es el usuario, sus roles, `idCliente`, cuándo expira), firmado con una **clave privada** que solo conoce el sistema que lo emitió. Cualquier instancia puede verificar esa firma con la clave pública correspondiente, sin consultarle nada a `pagatu-auth-ms` en cada petición ni a ninguna base de datos — por eso es **stateless**: el propio token es la prueba, no una referencia a un estado guardado en otro lado. Quien tiene la clave pública puede **comprobar** un token, pero no puede **fabricar** uno.

**Tabla 2. Autenticación con sesión vs. autenticación con JWT**

| | Con sesión (stateful) | Con JWT (stateless) |
|---|---|---|
| Dónde vive el estado de "quién está autenticado" | En el servidor (memoria o BD de sesiones) | Dentro del propio token, firmado |
| Qué necesita cada instancia para validar | Consultar el mismo almacén de sesiones que las demás | Solo la clave pública del emisor (se descarga una vez) — no depende de las demás instancias |
| Qué pasa si el balanceador manda la petición a otra instancia | Falla, salvo que las instancias compartan el almacén de sesiones | Funciona igual: cualquier instancia valida el mismo token |
| Cómo se revoca antes de que expire | Borrando la sesión del almacén | No es trivial (2.2, Error frecuente) |

**Error frecuente**: asumir que un JWT se puede "cerrar sesión" como una sesión tradicional. Como el token no depende de ningún estado en el servidor, invalidarlo antes de su expiración natural exige un mecanismo aparte (una lista negra de tokens revocados, por ejemplo) — fuera del alcance de esta sesión. Por eso `jwt.expiracion-segundos` (3.11) debe ser un valor corto en un sistema real; en esta sesión se deja largo (una hora) solo para no complicar las pruebas manuales.

### 2.3 Autorización basada en roles

Autenticación responde *quién eres*; autorización responde *qué puedes hacer* — son dos preguntas distintas, y un JWT válido solo resuelve la primera. Un `CLIENTE` autenticado con un JWT perfectamente válido no debería poder borrar un producto del catálogo — ese JWT prueba su identidad, no le da permiso para esa acción.

Esta sesión usa **RBAC** (*Role-Based Access Control*): los permisos no se asignan usuario por usuario, sino a **roles**, y a cada usuario se le asignan uno o más roles. Así, cambiar lo que puede hacer una persona (porque cambió de puesto, o porque dejó la organización) es cambiarle el rol, no buscar permiso por permiso. Por eso los roles viven en su **propia tabla** (`roles`) y se relacionan con `usuarios` de muchos a muchos (3.3): agregar un rol nuevo, o darle dos roles a un mismo usuario, es agregar filas, no cambiar el esquema. Los roles del usuario viajan dentro del JWT como el claim `realm_access.roles` (`["ADMIN"]`, `["CLIENTE"]`), y cada ruta del Gateway declara qué rol necesita (3.15) — `hasRole("ADMIN")` en vez de una lista de usuarios autorizados uno por uno, que no escalaría a medida que el sistema crece.

**Figura 3. Comprobación RBAC en `pagatu-gateway`: el rol del usuario decide si la petición pasa**

```mermaid
flowchart TB
    Usuario(["Usuario<br/>cliente@pagatu.com"])
    Rol["Rol: CLIENTE"]
    P1["ordenes: crear y consultar"]
    P2["productos: consultar"]
    Peticion(["POST /api/v1/productos<br/>con el token de CLIENTE"])
    Check{"¿el rol tiene permiso<br/>para esta ruta?"}
    Denegado["403 Forbidden"]
    Permitido["Petición autorizada"]

    Usuario -->|"tiene el rol"| Rol
    Rol -->|"permisos"| P1
    Rol -->|"permisos"| P2
    Peticion --> Check
    Check -->|"productos: escribir<br/>no asignado"| Denegado
    Check -->|"permiso presente"| Permitido
```

*Nota.* Adaptado de *RBAC / Control de Acceso*, por SACAViX, s. f.-a, System Design (https://systemdesign.sacavix.com/patterns).

Un **permiso** es una acción sobre un recurso (`ordenes:crear`, `productos:escribir`); un **rol** es un paquete de permisos que se asigna a personas. En esta sesión los permisos no se guardan en ninguna tabla: están implícitos en las reglas por ruta de `SecurityConfig` (3.15) — cada `hasRole(...)` dice "este rol tiene permiso para esta ruta y este método HTTP". Si el sistema crece, ese es el punto donde conviene separar rol y permiso en dos conceptos distintos.

Cuando la autorización falla, el código HTTP dice **cuál** de las dos preguntas falló: `401 Unauthorized` significa que no se pudo confirmar *quién eres* (sin token, token mal formado, firma inválida o expirado); `403 Forbidden` significa que sí se confirmó, pero ese rol no alcanza para esa ruta.

RBAC es simple, y eso lo hace popular, pero tiene límites que conviene conocer:

- **Explosión de roles:** si cada combinación de condiciones se convierte en un rol nuevo (`CLIENTE_LIMA_PREMIUM`, `CLIENTE_CUSCO_BASICO`...), los roles dejan de poder gestionarse.
- **No maneja contexto dinámico:** "un `CLIENTE` solo puede ver **sus propias** órdenes" no se expresa con un rol, sino comparando un atributo del usuario con un atributo del recurso. Ese enfoque se llama **ABAC** (*Attribute-Based Access Control*): más flexible y más complejo. El claim `idCliente` de hoy ya es un atributo, y compararlo con el dueño del recurso es el primer paso hacia ABAC (4.1, "consultar el propio perfil").
- **Requiere revisiones periódicas:** quién tiene qué rol cambia con el tiempo, y nadie lo revisa solo (3.24).

Y los errores de diseño más comunes al aplicarlo:

- Asignar permisos directamente a usuarios, en vez de a roles — vuelve al problema original de no escalar.
- Crear roles demasiado amplios "por comodidad" (un `ADMIN` para todo), porque es más fácil que pensar qué necesita cada persona.
- No hacer revisiones periódicas: roles acumulados que nadie revisó en años.
- No registrar en un *audit log* las verificaciones de autorización, y quedarse sin evidencia cuando hay que investigar un incidente.

### 2.4 OAuth2 y OpenID Connect: el estándar detrás de un proveedor de identidad

**OAuth 2.0** es un estándar de **autorización delegada** (RFC 6749): define cómo una aplicación obtiene acceso limitado a un recurso protegido sin conocer la contraseña del usuario, presentando un **token** emitido por un servidor de autorización en el que ambos confían. **OpenID Connect (OIDC)** es una capa de identidad encima de OAuth 2.0: agrega un formato estándar para decir *quién es el usuario* (el *ID Token*) y un mecanismo de descubrimiento (dónde están las claves, dónde se pide el token). Un proveedor de identidad como **Keycloak** implementa ambos estándares — por eso lo que se aprende hoy con Spring Security no se tira cuando llega Keycloak: cambia quién emite el token, no cómo se valida.

Estos conceptos se confunden con facilidad, así que conviene fijar hasta dónde llega cada uno. **SSO** (*Single Sign-On*) no es un estándar aparte sino un **resultado**: iniciar sesión una sola vez y entrar a varias aplicaciones sin volver a escribir la contraseña. Es posible porque la sesión vive en el servidor de identidad (una cookie en ese servidor), y cada aplicación lo consulta mediante OpenID Connect en vez de pedir credenciales por su cuenta. **OAuth 2.0 por sí solo no da SSO**: resuelve la autorización, no la sesión compartida.

**Tabla 3. Alcance de cada pieza: estándar, librería y producto**

| Pieza | Qué es | Qué resuelve | En esta sesión |
|---|---|---|---|
| OAuth 2.0 | Estándar de autorización delegada. | Que una aplicación acceda a una API con un token, sin conocer la contraseña. | Se implementa el lado *Resource Server*: el Gateway y `pagatu-orden-ms` validan tokens. `pagatu-auth-ms` **no** es un servidor OAuth 2.0 completo. |
| OpenID Connect | Capa de identidad sobre OAuth 2.0. | Saber quién inició sesión (*ID Token*), y publicar el descubrimiento y las claves. | Solo conceptual. `pagatu-auth-ms` publica claves (JWKS) y claims parecidos, sin *ID Token* ni descubrimiento. |
| SSO | Resultado: una sola sesión para varias aplicaciones. | No volver a iniciar sesión en cada aplicación. | **No existe hoy**: `pagatu-auth-ms` no tiene sesión central. Llega con Keycloak (cliente Angular, S11). |
| Spring Security 7 | Librería de seguridad de Spring. | Autenticación, autorización por rol, *resource server* y cliente OAuth 2.0/OIDC. Desde la versión 7 incluye además un servidor de autorización. | Se usa para autenticar con usuario y contraseña, validar tokens en el Gateway y en `pagatu-orden-ms`, y emitir el JWT simple de `pagatu-auth-ms`. **No** se usa su servidor de autorización. |
| Keycloak | Producto: proveedor de identidad (servidor). | Servidor OAuth 2.0/OIDC completo: usuarios, roles, sesión y SSO, *refresh token*, MFA, federación y consola de administración. | No se usa en la sesión; reemplaza a `pagatu-auth-ms` después (Tabla 6). |

La regla práctica: **Spring Security 7 se usa para proteger tus APIs (validar tokens); Keycloak se usa para emitir tokens y dar SSO.** Spring Security 7 también podría ser el servidor de identidad, pero construir con él la sesión central, el cierre de sesión global, MFA, federación y la consola de administración equivale a reescribir Keycloak — por eso no se hace en este curso.

**Tabla 4. Los cuatro actores de OAuth 2.0 en esta sesión**

| Actor | Qué es | En esta sesión | Con Keycloak |
|---|---|---|---|
| *Resource Owner* | La persona dueña de los datos que se protegen. | El usuario `cliente@pagatu.com`. | Igual. |
| *Client* | La aplicación que actúa en nombre del usuario y presenta el token. | PowerShell / `curl` hoy; Angular en S11. | Igual — se registra como *client* del *realm*. |
| *Authorization Server* | Autentica al usuario y **emite** los tokens. | `pagatu-auth-ms` (temporal). | Keycloak. |
| *Resource Server* | La API que **valida** el token y protege sus recursos. | `pagatu-gateway` y `pagatu-orden-ms`. | Igual — no cambia de código. |

**Figura 4. Los cuatro actores de OAuth 2.0 y el recorrido de un token**

```mermaid
flowchart TB
    Usuario["Resource Owner<br/>el usuario"]
    Cliente["Client<br/>PowerShell hoy<br/>Angular en S11"]
    AS["Authorization Server<br/>HOY: pagatu-auth-ms<br/>DESPUÉS: Keycloak"]
    RS["Resource Server<br/>pagatu-gateway<br/>pagatu-orden-ms"]

    Usuario -->|"entrega sus credenciales"| Cliente
    Cliente -->|"1. pide un token"| AS
    AS -->|"2. access_token (JWT)"| Cliente
    Cliente -->|"3. Authorization: Bearer"| RS
    RS -. "4. verifica la firma con la<br/>clave pública (JWKS)" .-> AS
```

Un proveedor de identidad completo emite tres tipos de token, y hoy solo construimos uno:

- **Access token**: el que las APIs aceptan (`Authorization: Bearer ...`). Es de vida corta. Es el único que emite `pagatu-auth-ms` — su campo `access_token` y `expires_in` (en segundos) siguen los nombres del estándar a propósito.
- **Refresh token**: permite pedir un access token nuevo sin volver a pedir la contraseña. Hoy no existe; Keycloak lo emite.
- **ID Token** (OIDC): le dice al *client* quién inició sesión. No se manda a las APIs. Hoy no existe; Keycloak lo emite.

#### Cómo obtiene un token cada tipo de *client*

Un **flujo** (*grant type*) define cómo un *client* obtiene el token, y elegirlo depende de dos preguntas: ¿hay una **persona** presente que pueda iniciar sesión?, y ¿el *client* puede **guardar un secreto** sin que nadie lo vea? Un servidor con su propio backend sí puede (*confidential client*); una aplicación web que corre en el navegador, o una app móvil, no (*public client*): cualquiera puede inspeccionar su código.

**Tabla 5. Flujos de OAuth 2.0 y cuándo se usa cada uno**

| Flujo | ¿Hay persona? | ¿El *client* guarda un secreto? | Cuándo se usa | En pagatu |
|---|---|---|---|---|
| *Authorization Code* | Sí | Sí (*confidential client*) | Aplicación web con backend propio: el navegador va al servidor de autorización, la aplicación recibe un `code` de un solo uso y su backend lo canjea por tokens con su `client_secret`. | No se usa hoy. |
| *Authorization Code* + **PKCE** | Sí | No (*public client*) | Aplicaciones que no pueden proteger un secreto (SPA, apps móviles). Hoy es la recomendación estándar, incluso para aplicaciones con backend. | Es el flujo del cliente Angular con Keycloak (S11). |
| *Client Credentials* | No | Sí | Comunicación servicio a servicio: el servicio se autentica con su propio `client_id` y `client_secret`, y el token lo representa a **él**, no a una persona. | Ninguna llamada de hoy lo necesita (la llamada Feign de S6 consulta un endpoint público). |
| *Device Code* (RFC 8628) | Sí, desde **otro** dispositivo | No | Dispositivos sin teclado o navegador cómodo (televisores, consolas, terminales): el dispositivo muestra un código y la persona lo ingresa desde su celular. | No aplica al proyecto. |
| *Resource Owner Password* | Sí | — | El *client* recibe el usuario y la contraseña y los envía al servidor. **No usar** (RFC 9700). | El `POST /api/v1/auth/login` de hoy se parece a este flujo. |

*Nota.* Adaptado de *OAuth2 / OpenID Connect*, por SACAViX, s. f.-b, System Design (https://systemdesign.sacavix.com/patterns).

**Authorization Code.** Lo importante es **por dónde viaja cada cosa**: el navegador solo ve el `code` (que por sí solo no sirve), y el canje por tokens ocurre de servidor a servidor, con el `client_secret`, sin pasar nunca por el navegador. Por eso es el flujo más seguro cuando hay un backend que pueda guardar ese secreto.

**Figura 5. Flujo *Authorization Code***

```mermaid
sequenceDiagram
    actor U as Usuario
    participant A as Aplicación web (backend)
    participant AS as Servidor de autorización
    participant API as API protegida

    U->>AS: 1. login (redirigido desde la aplicación)
    AS-->>A: 2. redirect + code (un solo uso)
    A->>AS: 3. code + client_secret
    AS-->>A: 4. access_token
    A->>API: 5. Authorization: Bearer access_token
    API-->>A: 6. 200 OK
```

*Nota.* Adaptado de *OAuth2 / OpenID Connect*, por SACAViX, s. f.-b, System Design (https://systemdesign.sacavix.com/patterns).

**Authorization Code + PKCE.** Una SPA no puede guardar un `client_secret`, así que **PKCE** (*Proof Key for Code Exchange*, RFC 7636) lo reemplaza por un valor aleatorio que el *client* inventa en **cada** inicio de sesión: el `code_verifier`. El *client* envía primero su hash (`code_challenge`) y, al canjear el `code`, debe presentar el `code_verifier` original; el servidor calcula el hash y comprueba que coincide. Aunque alguien intercepte el `code` en el redirect, no puede canjearlo, porque no conoce el `code_verifier` y un hash no se puede invertir (3.25 lo muestra con datos reales).

**Figura 6. Flujo *Authorization Code* con PKCE**

```mermaid
sequenceDiagram
    actor U as Usuario
    participant SPA as SPA (Angular)
    participant AS as Servidor de autorización
    participant API as API protegida

    Note over SPA: genera un code_verifier aleatorio<br/>y calcula code_challenge = SHA-256(code_verifier)
    SPA->>AS: 1. redirect + code_challenge
    U->>AS: 2. login
    AS-->>SPA: 3. redirect + code
    SPA->>AS: 4. code + code_verifier
    Note over AS: comprueba que SHA-256(code_verifier)<br/>coincide con el code_challenge del paso 1
    AS-->>SPA: 5. access_token + refresh_token
    SPA->>API: 6. Authorization: Bearer access_token
    API-->>SPA: 7. 200 OK
```

*Nota.* Adaptado de *OAuth2 / OpenID Connect*, por SACAViX, s. f.-b, System Design (https://systemdesign.sacavix.com/patterns).

**Client Credentials.** No hay persona ni redirects: un servicio le pide un token al servidor de autorización con su propia identidad. Sirve para llamadas entre servicios "por cuenta propia", sin un usuario detrás.

**Figura 7. Flujo *Client Credentials***

```mermaid
sequenceDiagram
    participant A as Servicio A
    participant AS as Servidor de autorización
    participant B as Servicio B (API protegida)

    A->>AS: 1. client_id + client_secret
    AS-->>A: 2. access_token (representa al servicio)
    A->>B: 3. Authorization: Bearer access_token
    B-->>A: 4. 200 OK
```

*Nota.* Adaptado de *OAuth2 / OpenID Connect*, por SACAViX, s. f.-b, System Design (https://systemdesign.sacavix.com/patterns).

El `POST /api/v1/auth/login` que construyes hoy, donde el *client* envía el usuario y la contraseña directo al servidor, equivale al flujo *Resource Owner Password Credentials*: sirve para aprender, pero la mejor práctica vigente de seguridad de OAuth 2.0 (RFC 9700) dice que **no debe usarse**, y OAuth 2.1 lo elimina. Por eso queda como pieza temporal de laboratorio, no como diseño final: con Keycloak, la persona escribe su contraseña en la pantalla de Keycloak, nunca en la aplicación.

No confundas *rol* con *scope*: un **rol** es un atributo del usuario (`ADMIN`, `CLIENTE`) y se usa hoy; un **scope** es un permiso que el *client* pide en nombre del usuario (por ejemplo, `ordenes:leer`) y queda fuera del alcance de esta sesión, aunque Keycloak lo soporta.

**Lo que cuesta usar OAuth 2.0.** Es el estándar correcto, pero no es gratis: los flujos son más complejos (redirects, varios tokens); el servidor de autorización (Keycloak, Auth0) pasa a ser un componente **crítico** — si cae, nadie inicia sesión —; hay que gestionar tokens (refresh, rotación, revocación); y depurar un flujo de autorización es difícil porque el problema puede estar en cualquiera de los actores. Con *Client Credentials* hay un riesgo extra: no involucra a ninguna persona, así que un `client_secret` comprometido da acceso total a la identidad de ese servicio.

**Errores de diseño más comunes** al usarlo (todos aplican cuando llegue el cliente Angular de S11):

- Usar el flujo *Implicit*: obsoleto y vulnerable a la filtración de tokens; RFC 9700 desaconseja usarlo.
- No validar el parámetro `state` en el redirect, que protege contra CSRF.
- Guardar *access tokens* en `localStorage`, vulnerable a XSS. La alternativa habitual es guardarlos en memoria y dejar el *refresh token* en una cookie `httpOnly`.
- No rotar los *refresh tokens*, de modo que uno robado sirve indefinidamente.
- Usar *Authorization Code* **sin PKCE** en un cliente público (SPA, app móvil) que no puede proteger un `client_secret`.

Por último, la tabla que resume qué de todo esto reemplaza Keycloak:

**Tabla 6. Qué reemplaza Keycloak de `pagatu-auth-ms`, y qué código cambia**

| Pieza | Hoy: `pagatu-auth-ms` | Con Keycloak | ¿Cambia el código del Gateway / `pagatu-orden-ms`? |
|---|---|---|---|
| Servidor de autorización | `pagatu-auth-ms` (puerto `8084`) | Keycloak, en un *realm* del proyecto | No |
| Cómo se pide el token | `POST /api/v1/auth/login` | `POST /realms/{realm}/protocol/openid-connect/token` | No — lo llama el *client*, no los servicios |
| Usuarios, contraseñas y roles | Tablas `usuarios`, `roles`, `usuario_roles` (Flyway, 3.3) | Usuarios y *realm roles* del *realm* | No |
| Claim de roles | `realm_access.roles` | `realm_access.roles` (el mismo formato) | No — el conversor de 3.14 sirve tal cual |
| Identificador del usuario (`sub`) | `id` numérico de `usuarios` | UUID del usuario | No |
| `idCliente` | Columna `id_cliente` de `usuarios` | *User attribute* + *protocol mapper* que lo agrega al token | No — el claim se llama igual |
| Claves de firma | Par RSA generado al arrancar; **se pierde al reiniciar** | Administradas, persistidas y rotadas por Keycloak | No |
| Dónde descargan la clave pública los *resource servers* | `jwk-set-uri: http://localhost:8084/.well-known/jwks.json` | `issuer-uri: http://localhost:{puerto}/realms/{realm}` | **Sí** — una propiedad YAML |
| Refresh token, ID Token, cierre de sesión, SSO, MFA | No existen | Incluidos | — |

El único cambio de configuración de la penúltima fila tiene una consecuencia extra: con `issuer-uri`, Spring descubre solo la ubicación de las claves (Keycloak publica un documento de descubrimiento OIDC) y además valida que el claim `iss` del token coincida con ese emisor. Hoy configuramos `jwk-set-uri` a mano porque `pagatu-auth-ms` no implementa el documento de descubrimiento.

### 2.5 Resource Server y validación de tokens con claves públicas

Un ***Resource Server*** es una aplicación que protege recursos y acepta *access tokens* como prueba de acceso. **No emite tokens ni guarda contraseñas: solo los valida.** Validar un JWT significa comprobar, como mínimo, tres cosas: que la **firma** es auténtica, que el token **no expiró** (`exp`) y, si el emisor lo define, que viene del **emisor** esperado (`iss`).

Hay dos formas de firmar un JWT, y la diferencia importa para un sistema distribuido:

- **Simétrica (HS256):** una única clave secreta firma *y* verifica. Todo servicio que valida tiene que **conocer el secreto** — y quien conoce el secreto también puede fabricar un token de `ADMIN`. Con diez microservicios, hay diez lugares desde donde se puede filtrar la clave que rompe todo el sistema.
- **Asimétrica (RS256):** un **par de claves**. La privada firma y solo la conoce el emisor; la pública verifica y puede estar en cualquier lugar. Los *resource servers* no guardan ningún secreto: si alguien compromete el Gateway, no obtiene nada con qué fabricar tokens. Es la forma que usa Keycloak, y la que usa esta sesión.

La clave pública se publica en un documento JSON llamado **JWKS** (*JSON Web Key Set*), en una URL conocida. Cada clave lleva un identificador (`kid`), y el encabezado de cada JWT dice con qué `kid` fue firmado — así el emisor puede **rotar** claves sin romper los tokens vigentes. Spring Security descarga el JWKS, lo guarda en memoria y lo vuelve a pedir solo cuando aparece un `kid` desconocido.

En un sistema de microservicios, cada *resource server* valida el token **por su cuenta**, en vez de confiar en que otro ya lo hizo. El Gateway es la primera línea (rechaza temprano y aplica el rol por ruta); el microservicio es la segunda (no depende de que nadie más haya verificado nada). Esa es la respuesta a "¿y si alguien llama directo al microservicio, sin pasar por el Gateway?" (1.6.1, pregunta de comprensión 1).

Decodificar un JWT no es validarlo. Un JWT es solo tres partes en Base64 — cualquiera puede leer sus claims, y cualquiera puede escribir un token con los claims que quiera. Lo que lo hace confiable es la **firma verificada**; leer `idCliente` de un token sin verificarla es exactamente el problema de 1.6.1, con un paso más.

### 2.6 Observabilidad y diagnóstico

Cuando una petición falla con `401` o `403`, el problema puede estar en tres lugares distintos, y diagnosticarlo bien depende de saber cuál:

1. **El JWT no se envió o está mal formado** — revisa el header `Authorization` que realmente salió del cliente (`Bearer ` + token, sin comillas ni espacios de más).
2. **El JWT no se pudo verificar o expiró** — revisa que `jwk-set-uri` apunte de verdad al JWKS de `pagatu-auth-ms` (ábrelo en el navegador), que `pagatu-auth-ms` esté arriba cuando llega el primer token (sin él, el *resource server* no puede descargar la clave y rechaza todo), y que no lo hayas **reiniciado** desde que pediste el token (3.7: el par de claves se regenera al arrancar, y los tokens anteriores dejan de verificar).
3. **El JWT es válido pero el rol no alcanza para esa ruta** — revisa la regla de `SecurityConfig` (3.15) contra los roles reales que trae el claim `realm_access.roles`.

Para distinguir estos tres casos sin adivinar, sube el nivel de log de seguridad mientras diagnosticas (`logging.level.org.springframework.security: DEBUG` en `pagatu-gateway`): el log dice el motivo exacto del rechazo. Los logs del Gateway (3.17) y el `traceId` de cada petición (mismo `CorrelationIdFilter` de S1/S6, si ya lo replicaste en `pagatu-auth-ms`) son el punto de partida.

## 3. Aplica: actividad práctica guiada

Tiempo: 4h.

**Actividad:** construcción guiada de `pagatu-auth-ms` (un servidor de autorización didáctico, con usuarios y roles en tablas propias), protección de `pagatu-gateway` como Resource Server, y conversión de `pagatu-orden-ms` en Resource Server que toma `idCliente` del JWT ya validado (Producto de la sesión en 1.4).

**Propósito de la actividad:** que cada estudiante implemente autenticación stateless con un JWT firmado con clave asimétrica y autorización basada en roles, validando el token tanto en el Gateway como dentro del microservicio, y verificando con evidencia real accesos permitidos y denegados — no solo el caso feliz.

**Orientaciones metodológicas:** en el laboratorio, el docente construye las tres partes de construcción de la sesión en orden frente a la clase — primero `pagatu-auth-ms` completo (Parte A), después la protección del Gateway (Parte B), al final la conversión de `pagatu-orden-ms` (Parte C) —, y cierra con una revisión de accesos y un ejercicio de PKCE (Parte D); los estudiantes replican cada paso en su propio equipo, y provocan ellos mismos los casos denegados (3.17, 3.22) para ver el `401`/`403` real en su propia consola, no solo leer el resultado esperado en la guía. `pagatu-auth-ms` se construye a mano **a propósito** y es temporal: el objetivo es entender qué hace por dentro un servidor de autorización, para saber exactamente qué se le pide a Keycloak cuando lo reemplace (Tabla 6).

**Actividades para realizar:**

*Parte A — Construir `pagatu-auth-ms`:*

- **3.1** Crear el proyecto base de `pagatu-auth-ms`.
- **3.2** Levantar la base de datos de `pagatu-auth-ms`.
- **3.3** Crear la migración Flyway con usuarios, roles y datos semilla.
- **3.4** Crear las entidades `Usuario` y `Rol`.
- **3.5** Crear los DTO de login.
- **3.6** Crear el repositorio y el servicio que carga usuarios en Spring Security.
- **3.7** Generar las claves RSA y crear el servicio de JWT.
- **3.8** Crear el servicio de autenticación, el controlador y el endpoint de claves públicas.
- **3.9** Configurar Spring Security en `pagatu-auth-ms`.
- **3.10** Conectar `pagatu-auth-ms` a `pagatu-config` y a `pagatu-eureka`.
- **3.11** Configurar `pagatu-auth-ms` en `config-repo`.
- **3.12** Levantar y probar `pagatu-auth-ms` de punta a punta.

*Parte B — Proteger `pagatu-gateway` como Resource Server:*

- **3.13** Agregar la dependencia de OAuth2 Resource Server.
- **3.14** Apuntar el Gateway a las claves públicas y configurar el conversor de roles.
- **3.15** Proteger las rutas del Gateway por rol.
- **3.16** Agregar la ruta de `pagatu-auth-ms` al Gateway.
- **3.17** Probar accesos permitidos y denegados a través del Gateway.

*Parte C — `pagatu-orden-ms` valida el JWT y toma el `idCliente` de él:*

- **3.18** Quitar `idCliente` del DTO de entrada.
- **3.19** Convertir `pagatu-orden-ms` en Resource Server.
- **3.20** Actualizar el controlador y el servicio de `pagatu-orden-ms`.
- **3.21** Probar de punta a punta, autenticado como `CLIENTE`.
- **3.22** Probar el llamado directo a `pagatu-orden-ms`, sin pasar por el Gateway.
- **3.23** Documentar la matriz de roles y accesos.

*Parte D — Verificar y entender lo que viene:*

- **3.24** Revisar los accesos y revocar un rol.
- **3.25** Ver PKCE en acción.

**Punto de partida común:** todo el equipo debe comenzar exactamente desde donde quedó S6 (Feign y Circuit Breaker), no desde su propio avance individual. Clona la rama `s06-feign-circuit-breaker`:

```bash
git clone --branch s06-feign-circuit-breaker https://github.com/262dist/pagatu.git
```

Levanta en DEV los servicios base ya construidos hasta S6 (`pagatu-config`, `pagatu-eureka`, `pagatu-gateway`, `pagatu-catalogo-ms`, `pagatu-orden-ms`) antes de tocar código nuevo — si alguno falla en arrancar, el problema es de una sesión anterior, no de esta.

### Parte A — Construir `pagatu-auth-ms`

#### 3.1 Crear el proyecto base de `pagatu-auth-ms`

**Producto del paso:** proyecto `pagatu-auth-ms` creado, con las mismas dependencias base que `pagatu-orden-ms` (S6) más Spring Security.

**Tabla 7. Configuración de `pagatu-auth-ms` en Spring Initializr**

| Campo | Valor |
|---|---|
| Project | Maven Project |
| Spring Boot | **4.1.1** |
| Language | Java |
| Group Id | `pe.edu.upeu` |
| Artifact Id | `pagatu-auth-ms` |
| Package name | `pe.edu.upeu.auth` |
| Packaging | Jar |
| Java | 21 |
| Dependencias | Spring Web, Validation, Lombok, Spring Boot DevTools, SpringDoc OpenAPI WebMvc UI, Spring Boot Actuator, Spring Data JPA, PostgreSQL Driver, Flyway, **Spring Security** — las mismas de `pagatu-orden-ms` (S6, Tabla 3) más Spring Security, nueva hoy. |
| Ubicación sugerida | `services/pagatu-auth-ms` |

Agrega también a mano, en el `pom.xml`, la librería con la que Spring Security **firma** un JWT — Spring Initializr no la ofrece como opción por separado (mismo criterio que MapStruct en S1, 3.5.20):

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-oauth2-jose</artifactId>
</dependency>
```

Sin `<version>`: la gestiona el padre `spring-boot-starter-parent`. Trae `NimbusJwtEncoder` (para emitir tokens) y la librería Nimbus JOSE que sabe generar y publicar claves. **No se agrega ninguna librería JWT aparte:** la misma familia de Spring Security que valida el token en el Gateway y en `pagatu-orden-ms` es la que lo emite aquí — una sola manera de leer y escribir JWT en todo el sistema. Tampoco se agrega el *starter* de Resource Server: `pagatu-auth-ms` **emite** tokens, no valida los de otros, y ese *starter* activaría configuración automática que aquí no hace falta.

El puerto de base de datos (`15431` DEV / `25431` PROD local) y el nombre `pagatu_auth_db` ya estaban reservados desde la arquitectura del proyecto (`docs/index.md`) — no se inventan en esta sesión. El puerto de aplicación en DEV es `8084` — el siguiente libre después de `8080`/`8081` (`pagatu-catalogo-ms`, S1/S3) y `8082`/`8083` (`pagatu-orden-ms`, S6).

Si Spring Initializr todavía no ofrece **Spring Boot 4** como opción, agrega Spring Security a mano en el `pom.xml` después de generar el proyecto: `<artifactId>spring-boot-starter-security</artifactId>`, sin `<version>`. Revisa también, igual que en S6, que ningún starter conserve un nombre de Boot 3 (por ejemplo `spring-boot-starter-web` en vez de `spring-boot-starter-webmvc`).

#### 3.2 Levantar la base de datos de `pagatu-auth-ms`

**Producto del paso:** PostgreSQL de `pagatu-auth-ms` corriendo en DEV.

**`services/pagatu-auth-ms/compose-dev.yml`:**

```yaml
name: pagatu-auth-dev

services:
  postgres-auth-dev:
    image: postgres:16-alpine
    container_name: pagatu-postgres-auth-dev
    restart: unless-stopped
    environment:
      POSTGRES_DB: pagatu_auth_db
      POSTGRES_USER: pagatu
      POSTGRES_PASSWORD: pagatu
    ports:
      - "15431:5432"
    volumes:
      - pagatu_auth_dev_data:/var/lib/postgresql/data

volumes:
  pagatu_auth_dev_data:
```

PowerShell / bash macOS/Linux:

```bash
cd services/pagatu-auth-ms
docker compose -f compose-dev.yml up -d
```

Comprueba que la base de datos está lista, mismo criterio que S1/S6:

```bash
docker exec -it pagatu-postgres-auth-dev psql -U pagatu -d pagatu_auth_db -c "SELECT current_database();"
docker exec -it pagatu-postgres-auth-dev psql -U pagatu -d pagatu_auth_db -c "\dt"
```

Resultado esperado: `current_database` devuelve `pagatu_auth_db`, y `\dt` responde `No relations found.` — todavía no hay tablas, porque la migración Flyway (3.3) ni siquiera se ha creado.

#### 3.3 Crear la migración Flyway con usuarios, roles y datos semilla

**Producto del paso:** tres tablas — `usuarios`, `roles` y la tabla intermedia `usuario_roles` —, con dos usuarios de prueba: uno `ADMIN`, uno `CLIENTE`.

Los roles viven en su **propia tabla**, y `usuario_roles` los relaciona con los usuarios de muchos a muchos (2.3): un usuario puede tener varios roles, y un rol nuevo es una fila más, no una columna ni un `enum` que obligue a recompilar. Es, además, el mismo modelo que usa Keycloak (un usuario, varios *realm roles*), lo que hace la migración de la Tabla 6 casi directa.

**`services/pagatu-auth-ms/src/main/resources/db/migration/V1__create_usuarios_roles.sql`:**

```sql
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_roles_nombre UNIQUE (nombre)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(100) NOT NULL,
    habilitado BOOLEAN NOT NULL DEFAULT TRUE,
    id_cliente BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_roles_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_roles_rol
        FOREIGN KEY (rol_id) REFERENCES roles (id) ON DELETE CASCADE
);

INSERT INTO roles (nombre) VALUES ('ADMIN'), ('CLIENTE');

INSERT INTO usuarios (email, password, habilitado, id_cliente) VALUES
    ('admin@pagatu.com', '$2b$10$ndX5v/xbbP8LAFlts57QweeqsmxNNDTkWZG4wpmShADMaVTON9bfC', TRUE, NULL),
    ('cliente@pagatu.com', '$2b$10$zCONDt0UNbJJh4C936JZyubU7ceojmoItVznixqKnivxjuxsofAPC', TRUE, 1);

INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
JOIN roles r ON (u.email = 'admin@pagatu.com' AND r.nombre = 'ADMIN')
             OR (u.email = 'cliente@pagatu.com' AND r.nombre = 'CLIENTE');
```

El rol se guarda **sin** el prefijo `ROLE_` (`ADMIN`, no `ROLE_ADMIN`) — igual que Keycloak. Spring Security sí exige ese prefijo internamente para `hasRole(...)`, y se agrega en los dos lugares donde se traduce el rol a autoridad de Spring (3.6 y 3.14), nunca en la base de datos.

Las contraseñas ya están hasheadas con BCrypt (nunca se guarda una contraseña en texto plano, ni siquiera en datos semilla de práctica): el usuario `admin@pagatu.com` tiene contraseña real `admin123`, y `cliente@pagatu.com` tiene `cliente123` — verificados de antemano contra esos dos hashes exactos. `id_cliente: 1` en el usuario `CLIENTE` es el mismo `idCliente` que S6 usaba a mano en el request (3.20 lo reemplaza por este valor, tomado del JWT en vez de escrito por quien llama). Ojo: `id_cliente` es un dato **de negocio** que este servicio guarda por comodidad, no un dato de identidad — con Keycloak pasa a ser un atributo del usuario (Tabla 6).

**Si necesitas generar tu propio hash** (por ejemplo, para el trabajo autónomo de `pagatu-cliente-ms`, 4.1), agrega temporalmente este endpoint en `AuthController` (3.8), pruébalo una vez, y **bórralo antes de entregar** — exponer un generador de hashes en un endpoint público es un riesgo de seguridad, no algo que quede en el proyecto final:

```java
@GetMapping("/_hash-temporal")
public String hashTemporal(@RequestParam String password) {
    return passwordEncoder.encode(password);
}
```

Para que compile, agrega `private final PasswordEncoder passwordEncoder;` a `AuthController` (`@RequiredArgsConstructor` lo inyecta solo).

#### 3.4 Crear las entidades `Usuario` y `Rol`

**Producto del paso:** mapeo JPA de las tres tablas.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/entity/Rol.java`:**

```java
package pe.edu.upeu.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
}
```

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/entity/Usuario.java`:**

```java
package pe.edu.upeu.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean habilitado = true;

    @Column(name = "id_cliente")
    private Long idCliente;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    @Builder.Default
    private Set<Rol> roles = new HashSet<>();
}
```

`fetch = FetchType.EAGER` porque los roles se necesitan **siempre** que se carga un usuario (para armar el JWT y las autoridades de Spring Security), y son pocos — es uno de los pocos casos donde traerlos de una vez es lo correcto. `idCliente` es `null` para un usuario `ADMIN` (no representa a ningún cliente) y tiene valor para un usuario `CLIENTE` — ese valor es el que viaja dentro del JWT y el que `pagatu-orden-ms` va a leer en la Parte C, en vez de confiar en el que el request declare.

#### 3.5 Crear los DTO de login

**Producto del paso:** contrato de entrada/salida del endpoint de login.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/dto/LoginRequest.java`:**

```java
package pe.edu.upeu.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
```

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/dto/LoginResponse.java`:**

```java
package pe.edu.upeu.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private long expiresIn;
}
```

Los nombres `access_token`, `token_type` y `expires_in` (este último **en segundos**) no son casualidad: son los del estándar OAuth 2.0 (2.4), los mismos que devuelve el *token endpoint* de Keycloak. Un cliente que hoy lee esta respuesta no necesita cambiar cómo interpreta el token el día que llegue Keycloak. `tokenType` siempre vale `"Bearer"` (3.8) — así el cliente sabe exactamente cómo debe mandar el token de vuelta: `Authorization: Bearer <token>`.

#### 3.6 Crear el repositorio y el servicio que carga usuarios en Spring Security

**Producto del paso:** Spring Security sabe buscar un usuario por su email y traducir sus roles a autoridades — la pieza que hace posible `AuthenticationManager` en 3.8.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/repository/UsuarioRepository.java`:**

```java
package pe.edu.upeu.auth.repository;

import pe.edu.upeu.auth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
```

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/service/UsuarioDetailsService.java`:**

```java
package pe.edu.upeu.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.edu.upeu.auth.entity.Usuario;
import pe.edu.upeu.auth.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .disabled(!usuario.isHabilitado())
                .authorities(usuario.getRoles().stream()
                        .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
                        .toList())
                .build();
    }
}
```

`UserDetailsService` es el contrato de Spring Security para "cómo se busca un usuario por su nombre de acceso" — aquí, el email. Al existir un único bean de este tipo, Spring Security arma solo el resto de la cadena: un proveedor de autenticación que llama a este método, compara la contraseña recibida contra el hash con el `PasswordEncoder` (3.9) y falla si no coinciden. Es la maquinaria estándar de Spring Security, no código propio de comparar contraseñas — y es la que el proyecto reemplaza por completo con Keycloak (Tabla 6). Aquí sí se agrega el prefijo `ROLE_`: es lo que espera `hasRole(...)` (3.15).

#### 3.7 Generar las claves RSA y crear el servicio de JWT

**Producto del paso:** un par de claves RSA en memoria, y el servicio que firma un JWT con la clave privada.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/config/JwtKeyConfig.java`:**

```java
package pe.edu.upeu.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class JwtKeyConfig {

    @Bean
    public RSAKey rsaKey() throws Exception {
        KeyPairGenerator generador = KeyPairGenerator.getInstance("RSA");
        generador.initialize(2048);
        KeyPair par = generador.generateKeyPair();

        return new RSAKey.Builder((RSAPublicKey) par.getPublic())
                .privateKey((RSAPrivateKey) par.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(RSAKey rsaKey) {
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }
}
```

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/service/JwtService.java`:**

```java
package pe.edu.upeu.auth.service;

import com.nimbusds.jose.jwk.RSAKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import pe.edu.upeu.auth.entity.Rol;
import pe.edu.upeu.auth.entity.Usuario;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final RSAKey rsaKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Getter
    @Value("${jwt.expiracion-segundos}")
    private long expiracionSegundos;

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        List<String> roles = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .sorted()
                .toList();

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(String.valueOf(usuario.getId()))
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(expiracionSegundos))
                .claim("preferred_username", usuario.getEmail())
                .claim("email", usuario.getEmail())
                .claim("realm_access", Map.of("roles", roles));

        if (usuario.getIdCliente() != null) {
            claims.claim("idCliente", usuario.getIdCliente());
        }

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(rsaKey.getKeyID())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }
}
```

Los nombres de los claims **no son arbitrarios**: `iss` (emisor), `sub` (identificador del usuario), `exp`/`iat` (vigencia), `preferred_username`, `email` y `realm_access.roles` son los mismos que emite Keycloak (Tabla 6). Por eso el conversor de roles del Gateway (3.14) funcionará tal cual cuando el token lo emita Keycloak. `idCliente` es un claim propio del proyecto: solo se agrega cuando no es `null` (un `ADMIN` no lo necesita).

`RS256` firma con la clave **privada** (`rsaKey`, que vive solo en este servicio); el encabezado del token lleva el `kid` para que quien lo reciba sepa con qué clave pública verificarlo (2.5). Esta clave se **genera al arrancar** el servicio: es suficiente para aprender, pero significa que cada vez que reinicies `pagatu-auth-ms`, los tokens emitidos antes dejan de verificar. Keycloak persiste y rota sus claves.

#### 3.8 Crear el servicio de autenticación, el controlador y el endpoint de claves públicas

**Producto del paso:** `POST /api/v1/auth/login` funcional y `GET /.well-known/jwks.json` publicando la clave pública.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/service/AuthService.java`:**

```java
package pe.edu.upeu.auth.service;

import pe.edu.upeu.auth.dto.LoginRequest;
import pe.edu.upeu.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
```

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/service/AuthServiceImpl.java`:**

```java
package pe.edu.upeu.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import pe.edu.upeu.auth.dto.LoginRequest;
import pe.edu.upeu.auth.dto.LoginResponse;
import pe.edu.upeu.auth.entity.Usuario;
import pe.edu.upeu.auth.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generarToken(usuario);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpiracionSegundos())
                .build();
    }
}
```

`authenticate(...)` lanza una excepción si las credenciales no son válidas — nunca devuelve "falso". Si llega a la línea siguiente, el usuario **ya está autenticado**, y recién ahí se lo vuelve a cargar para armar el JWT con su `id`, sus roles y su `idCliente`. Spring Security ya responde igual (`BadCredentialsException`, mensaje `"Bad credentials"`) tanto si el email no existe como si la contraseña no coincide: no filtra qué emails están registrados, sin que tengas que programarlo.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/exception/GlobalExceptionHandler.java`:**

```java
package pe.edu.upeu.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales invalidas"));
    }
}
```

El mensaje es **fijo**, sin importar la causa (contraseña incorrecta, usuario inexistente o cuenta deshabilitada): revelar cuál falló le regala información a quien intenta adivinar credenciales ajenas.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/controller/AuthController.java`:**

```java
package pe.edu.upeu.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.auth.dto.LoginRequest;
import pe.edu.upeu.auth.dto.LoginResponse;
import pe.edu.upeu.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
```

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/controller/JwksController.java`:**

```java
package pe.edu.upeu.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwksController {

    private final RSAKey rsaKey;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> jwks() {
        return new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
    }
}
```

`toPublicJWK()` descarta la parte privada: este endpoint **solo** puede exponer la clave pública. Es el equivalente a `/realms/{realm}/protocol/openid-connect/certs` de Keycloak (Tabla 6) — la URL a la que el Gateway y `pagatu-orden-ms` van a ir a descargar la clave para verificar firmas.

#### 3.9 Configurar Spring Security en `pagatu-auth-ms`

**Producto del paso:** `/api/v1/auth/login` y `/.well-known/jwks.json` accesibles sin autenticación previa (tiene sentido: nadie tiene un JWT todavía antes de hacer login, y la clave pública es pública por definición), y el `PasswordEncoder` y el `AuthenticationManager` disponibles para inyectar.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/config/SecurityConfig.java`:**

```java
package pe.edu.upeu.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());
        return http.build();
    }
}
```

`SessionCreationPolicy.STATELESS` le indica a Spring Security que no cree ninguna sesión HTTP: cada petición se valida por sí sola (2.2). `csrf` se desactiva porque esa protección existe para sesiones con cookies — sin cookies de sesión, no hay nada que proteger. `pagatu-auth-ms` solo expone endpoints públicos (login, claves, *health*), por eso `permitAll()`.

**Error frecuente**: agregar `spring-boot-starter-security` al `pom.xml` y no declarar ningún `SecurityFilterChain` propio. Spring Security se autoconfigura por defecto en cuanto detecta la dependencia — bloquea todo con un formulario de login y una contraseña generada al azar (visible en el log de arranque), en vez de dejar pasar libremente `/api/v1/auth/login`. Este `SecurityFilterChain` explícito reemplaza esa configuración por defecto.

#### 3.10 Conectar `pagatu-auth-ms` a `pagatu-config` y a `pagatu-eureka`

**Producto del paso:** `pagatu-auth-ms` externaliza su configuración y se registra como instancia descubrible.

**`services/pagatu-auth-ms/src/main/resources/application.yml`:**

```yaml
spring:
  application:
    name: pagatu-auth-ms
  profiles:
    active: dev
  config:
    import: "optional:configserver:${CONFIG_SERVER_URL:http://localhost:18888}"
```

Mismo patrón exacto que `pagatu-orden-ms` desde S6 — `optional:` evita que `pagatu-auth-ms` falle al arrancar si `pagatu-config` estuviera caído.

Agrega en el `pom.xml` las mismas dos dependencias de Spring Cloud que ya usa `pagatu-orden-ms` (S3, S6) — sin ellas, `pagatu-auth-ms` ni externaliza configuración ni se registra en Eureka:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

No olvides las `<properties>` de Spring Cloud, igual que en `pagatu-orden-ms`:

```xml
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2025.1.3</spring-cloud.version>
</properties>
```

y el `<dependencyManagement>` correspondiente (copia exacta del de `pagatu-orden-ms`, S6, 3.1).

#### 3.11 Configurar `pagatu-auth-ms` en `config-repo`

**Producto del paso:** el archivo de configuración DEV de `pagatu-auth-ms`, con los dos únicos datos propios del emisor de tokens: quién es (`issuer`) y cuánto duran sus tokens.

**`infra/pagatu-config/config-repo/pagatu-auth-ms-dev.yml`:**

```yaml
server:
  port: 8084

spring:
  datasource:
    url: jdbc:postgresql://localhost:15431/pagatu_auth_db
    username: pagatu
    password: pagatu
    driver-class-name: org.postgresql.Driver
  flyway:
    enabled: true
    locations: classpath:db/migration
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

springdoc:
  swagger-ui:
    path: /swagger-ui.html

logging:
  level:
    pe.edu.upeu.auth: DEBUG

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always

eureka:
  instance:
    hostname: localhost
    instance-id: ${spring.application.name}:${server.port}
  client:
    service-url:
      defaultZone: http://localhost:18761/eureka

jwt:
  issuer: http://localhost:8084
  expiracion-segundos: 3600
```

Mismo patrón exacto de `pagatu-orden-ms-dev.yml` (S6) — `ddl-auto: validate` porque el esquema real lo define Flyway (3.3), no Hibernate. `jwt.expiracion-segundos: 3600` es una hora, deliberadamente larga solo para no complicar las pruebas manuales de hoy (2.2, Error frecuente). **No hay ningún secreto compartido en `config-repo`:** ni el Gateway ni `pagatu-orden-ms` necesitan conocer nada que permita fabricar un token — solo saber de dónde descargar la clave pública (3.14, 3.19). Esa es la ventaja concreta de la firma asimétrica (2.5).

**Verifica** que el Config Server sirve el archivo correctamente antes de continuar:

PowerShell:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:18888/pagatu-auth-ms/dev"
```

bash macOS/Linux:

```bash
curl http://localhost:18888/pagatu-auth-ms/dev
```

**Si `propertySources` sale sin ningún `jwt.issuer`, no continúes.** Confirma que el archivo se llama exactamente `pagatu-auth-ms-dev.yml`, está directamente en `config-repo/` (no en una subcarpeta), y que `pagatu-config` lo recargó (reinicia `pagatu-config` si hace falta).

#### 3.12 Levantar y probar `pagatu-auth-ms` de punta a punta

**Producto del paso:** login real, con un JWT firmado, y la clave pública publicada.

```bash
cd services/pagatu-auth-ms
mvn spring-boot:run
```

Prueba con el usuario `ADMIN` semilla (3.3):

PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8084/api/v1/auth/login" `
  -ContentType "application/json" `
  -Body '{"email": "admin@pagatu.com", "password": "admin123"}'
```

bash macOS/Linux:

```bash
curl -X POST http://localhost:8084/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@pagatu.com", "password": "admin123"}'
```

Resultado esperado — `200 OK`:

```json
{
  "access_token": "eyJraWQiOiI0ZjJi...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

Prueba también con una contraseña incorrecta y confirma `401 Unauthorized` con `{"error": "Credenciales invalidas"}` — el manejador de 3.8 en acción. Guarda el `access_token` del `ADMIN` y repite el login con `cliente@pagatu.com` / `cliente123` — vas a necesitar **ambos** tokens en 3.17 y 3.21.

Ahora comprueba que la clave pública está publicada:

PowerShell:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8084/.well-known/jwks.json"
```

bash macOS/Linux:

```bash
curl http://localhost:8084/.well-known/jwks.json
```

Resultado esperado: un documento con una lista `keys`, y en ella una sola clave `RSA` con sus campos `kty`, `e`, `n` y `kid` — **sin** ningún campo privado (`d`, `p`, `q`).

**(Opcional) ¿Solo quieres inspeccionar un JWT sin escribir código?** Pega el token en [jwt.io](https://jwt.io) — el encabezado debe mostrar `"alg": "RS256"` y un `kid`, y el *payload* decodificado debe verse así para `admin@pagatu.com`:

```json
{
  "iss": "http://localhost:8084",
  "sub": "1",
  "iat": 1789699000,
  "exp": 1789702600,
  "preferred_username": "admin@pagatu.com",
  "email": "admin@pagatu.com",
  "realm_access": { "roles": ["ADMIN"] }
}
```

y, para `cliente@pagatu.com`, el mismo formato con `"realm_access": { "roles": ["CLIENTE"] }` más `"idCliente": 1`. jwt.io solo **lee** los claims; nunca pegues ahí un token de un sistema real.

### Parte B — Proteger `pagatu-gateway` como Resource Server

#### 3.13 Agregar la dependencia de OAuth2 Resource Server

**Producto del paso:** `pagatu-gateway` con capacidad de validar JWT.

**`infra/pagatu-gateway/pom.xml`:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security-oauth2-resource-server</artifactId>
</dependency>
```

Sin versión explícita — la gestiona `spring-boot-starter-parent`. Esta dependencia sola ya trae todo lo necesario para descargar las claves públicas, decodificar y validar un JWT (no hace falta agregar `spring-boot-starter-security` aparte, viene incluida de forma transitiva). En Spring Boot 4 el nombre de este *starter* cambió: el de Boot 3 (`spring-boot-starter-oauth2-resource-server`) todavía existe, pero está marcado como obsoleto — usa el nombre nuevo.

#### 3.14 Apuntar el Gateway a las claves públicas y configurar el conversor de roles

**Producto del paso:** `pagatu-gateway` capaz de verificar la firma de un JWT con la clave pública de `pagatu-auth-ms`, y de traducir el claim `realm_access.roles` a roles de Spring Security.

Primero, dile al Gateway **dónde** están las claves públicas. **`infra/pagatu-config/config-repo/pagatu-gateway-dev.yml`** — agrega esta propiedad **dentro del bloque `spring:` que ya existe** (no crees un segundo `spring:` en el mismo archivo — YAML no permite dos claves iguales en el mismo nivel):

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8084/.well-known/jwks.json
```

Con solo esa propiedad, Spring Boot arma el decodificador de JWT por su cuenta: descarga el JWKS la primera vez que llega un token, lo guarda en memoria, verifica la firma y la expiración de cada token que recibe. **No hay ningún bean `JwtDecoder` que escribir, ni ningún secreto que configurar.** Esta es, además, **la única línea que cambia el día que llegue Keycloak** (Tabla 6): `jwk-set-uri` se reemplaza por `issuer-uri`.

Ahora el conversor. **`infra/pagatu-gateway/src/main/java/pe/edu/upeu/gateway/config/SecurityConfig.java`** (primera mitad — el conversor de roles):

```java
package pe.edu.upeu.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            Object roles = realmAccess == null ? null : realmAccess.get("roles");
            if (!(roles instanceof Collection<?> lista)) {
                return List.<GrantedAuthority>of();
            }
            return lista.stream()
                    .map(rol -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + rol))
                    .toList();
        });
        return converter;
    }
    // continúa en 3.15
}
```

El conversor traduce cada elemento de `realm_access.roles` (`"ADMIN"`) al formato que Spring Security espera para autorizar por rol (`ROLE_ADMIN`) — sin este conversor, `hasRole("ADMIN")` (3.15) nunca encontraría ninguna autoridad que coincida, porque Spring Security por defecto busca el claim `scope`, no `realm_access.roles`. Como el formato del claim es **el mismo que emite Keycloak**, este bloque no se toca al migrar (Tabla 6); Keycloak agrega algunos roles propios (`offline_access`, `default-roles-...`) que el conversor simplemente traduce también, sin efecto en ninguna regla.

#### 3.15 Proteger las rutas del Gateway por rol

**Producto del paso:** reglas de autorización reales — quién puede hacer qué, por ruta y por método HTTP.

Completa el mismo archivo de 3.14, agregando el `SecurityFilterChain`:

```java
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                     JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/**", "/api/v1/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos/**", "/api/v1/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**", "/api/v1/categorias/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**", "/api/v1/categorias/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/ordenes/**").hasAnyRole("CLIENTE", "ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                );
        return http.build();
    }
}
```

Agrega los imports que faltan (`HttpMethod`, `HttpSecurity`, `SecurityFilterChain`) al inicio del archivo:

```java
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
```

Lectura de las reglas, en orden (Spring Security aplica la **primera** que coincide): `/api/v1/auth/**` y `/actuator/**` quedan abiertas — nadie tiene JWT antes de hacer login, y el *health check* no debería depender de tener uno. Consultar el catálogo (`GET`) es público — cualquiera puede mirar productos sin autenticarse. Modificar el catálogo (`POST`/`PUT`/`DELETE`) exige `ROLE_ADMIN`. Crear o consultar órdenes exige estar autenticado como `CLIENTE` o `ADMIN`. **Todo lo demás** (`anyRequest().authenticated()`) exige, como mínimo, un JWT válido — nada queda abierto por accidente, ni siquiera una ruta que esta sesión no previó.

#### 3.16 Agregar la ruta de `pagatu-auth-ms` al Gateway

**Producto del paso:** el Gateway sabe enrutar `/api/v1/auth/**` hacia `pagatu-auth-ms`.

**`infra/pagatu-config/config-repo/pagatu-gateway-dev.yml`** — agrega esta ruta a la lista que ya existe desde S4 (no reemplaces las otras):

```yaml
            - id: pagatu-auth-login
              uri: lb://pagatu-auth-ms
              predicates:
                - Path=/api/v1/auth/**
```

Por esta ruta pasa **solo el login**. La descarga de claves públicas (`/.well-known/jwks.json`) **no** pasa por el Gateway: el Gateway (y `pagatu-orden-ms`, 3.19) la piden directo a `pagatu-auth-ms`, tal como quedó en `jwk-set-uri` (3.14).

#### 3.17 Probar accesos permitidos y denegados a través del Gateway

**Producto del paso:** evidencia real de los cuatro casos — sin token, con token alterado, con rol incorrecto, con rol correcto.

Reinicia `pagatu-gateway` (para que lea la propiedad nueva) y levántalo:

```bash
cd infra/pagatu-gateway
mvn spring-boot:run
```

**Caso 1 — sin token, ruta protegida:**

PowerShell:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:18080/api/v1/ordenes"
```

bash macOS/Linux:

```bash
curl -i http://localhost:18080/api/v1/ordenes
```

Resultado esperado: `401 Unauthorized`.

**Caso 2 — token con la firma alterada:** copia el token del `ADMIN` (3.12), cámbiale **un solo carácter** cerca del final (la firma es la última parte) y envíalo a la misma ruta:

PowerShell:

```powershell
$tokenAlterado = "PEGA_AQUI_EL_TOKEN_CON_UN_CARACTER_CAMBIADO"

Invoke-RestMethod -Method Get -Uri "http://localhost:18080/api/v1/ordenes" `
  -Headers @{ Authorization = "Bearer $tokenAlterado" }
```

bash macOS/Linux:

```bash
TOKEN_ALTERADO="PEGA_AQUI_EL_TOKEN_CON_UN_CARACTER_CAMBIADO"

curl -i http://localhost:18080/api/v1/ordenes \
  -H "Authorization: Bearer $TOKEN_ALTERADO"
```

Resultado esperado: `401 Unauthorized` — el Gateway descargó la clave pública, la firma no coincide con el contenido, y el token se rechaza. Es la prueba de que un JWT **no se puede editar** (por ejemplo, para cambiar `"CLIENTE"` por `"ADMIN"`) sin invalidarlo.

**Caso 3 — token de `CLIENTE`, intentando modificar el catálogo (rol incorrecto):**

PowerShell:

```powershell
$tokenCliente = "PEGA_AQUI_EL_TOKEN_DE_CLIENTE_DE_3.12"

Invoke-RestMethod -Method Post -Uri "http://localhost:18080/api/v1/productos" `
  -Headers @{ Authorization = "Bearer $tokenCliente" } `
  -ContentType "application/json" `
  -Body '{"nombre": "Producto de prueba", "precio": 10.0}'
```

bash macOS/Linux:

```bash
TOKEN_CLIENTE="PEGA_AQUI_EL_TOKEN_DE_CLIENTE_DE_3.12"

curl -i -X POST http://localhost:18080/api/v1/productos \
  -H "Authorization: Bearer $TOKEN_CLIENTE" \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Producto de prueba", "precio": 10.0}'
```

Resultado esperado: `403 Forbidden` — el token es válido (pasó la firma), pero el rol `CLIENTE` no alcanza para `hasRole("ADMIN")`. Nota la diferencia con el caso 2: allá no se pudo confirmar **quién** eres (`401`); aquí sí, pero no tienes permiso (`403`, 2.3).

**Caso 4 — token de `ADMIN`, mismo endpoint (rol correcto):**

Repite el caso 3 con el token de `admin@pagatu.com`. Resultado esperado: `201 Created` (o el código que ya devuelva `pagatu-catalogo-ms` al crear un producto).

**Error frecuente**: copiar el token con comillas o espacios de más al pegarlo en la variable — el header queda mal formado y Spring Security lo rechaza como si no hubiera token, un `401` que en realidad es un error de copiado, no de configuración.

**Error frecuente**: `401` en **todas** las rutas protegidas, aun con un token recién emitido. Revisa que `pagatu-auth-ms` esté corriendo (sin él, el Gateway no puede descargar la clave pública), que `jwk-set-uri` (3.14) tenga el puerto y la ruta exactos, y que no hayas reiniciado `pagatu-auth-ms` **después** de pedir el token — el par de claves se regenera al arrancar (3.7), y el token anterior ya no verifica: pide uno nuevo (3.12).

### Parte C — `pagatu-orden-ms` valida el JWT y toma el `idCliente` de él

#### 3.18 Quitar `idCliente` del DTO de entrada

**Producto del paso:** `OrdenRequest` ya no acepta `idCliente` — quien crea una orden ya no puede decidir a nombre de quién.

**`services/pagatu-orden-ms/src/main/java/pe/edu/upeu/orden/dto/OrdenRequest.java`** — quita el campo `idCliente`:

```java
package pe.edu.upeu.orden.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenRequest {

    @NotBlank
    private String metodoPago;

    @NotEmpty
    @Valid
    private List<DetalleOrdenRequest> detalles;
}
```

Este es exactamente el cambio que 1.6.1 pedía: `idCliente` ya no es un dato que el cliente declara sobre sí mismo — 3.20 lo reemplaza por un valor que viene del JWT, imposible de falsificar sin la clave privada de `pagatu-auth-ms`.

#### 3.19 Convertir `pagatu-orden-ms` en Resource Server

**Producto del paso:** `pagatu-orden-ms` valida por su cuenta la firma de cada JWT que recibe, sin depender de que el Gateway ya lo haya hecho.

**`services/pagatu-orden-ms/pom.xml`:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security-oauth2-resource-server</artifactId>
</dependency>
```

Es exactamente la misma dependencia de 3.13. **`infra/pagatu-config/config-repo/pagatu-orden-ms-dev.yml`** — agrega la misma propiedad de 3.14 dentro del bloque `spring:` existente:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8084/.well-known/jwks.json
```

**`services/pagatu-orden-ms/src/main/java/pe/edu/upeu/orden/config/SecurityConfig.java`:**

```java
package pe.edu.upeu.orden.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
```

`pagatu-orden-ms` no repite las reglas por rol del Gateway (3.15) — esas son de la **primera línea**, por ruta. Aquí basta con exigir un token **válido**: `anyRequest().authenticated()`. Lo que sí hace esta segunda línea es lo más importante: verificar la firma por sí misma (2.5). Si alguien llama directo a `8082`, sin pasar por el Gateway, no obtiene ninguna ventaja (3.22).

`/actuator/**` queda abierto para que Eureka y las revisiones de *health* sigan funcionando, y Swagger UI para que la página cargue — pero **llamar** a `POST /api/v1/ordenes` desde Swagger ya no funciona sin un token: es exactamente el comportamiento buscado (el caso de 1.6.1). Las llamadas **salientes** de `pagatu-orden-ms` a `pagatu-catalogo-ms` (Feign, S6) no cambian: esta configuración solo protege lo que **entra**.

Un `401` de este filtro se responde **antes** de llegar al controlador, así que tu `GlobalExceptionHandler` de S1/S6 no interviene.

#### 3.20 Actualizar el controlador y el servicio de `pagatu-orden-ms`

**Producto del paso:** `crear()` recibe `idCliente` desde el JWT ya validado, nunca del cuerpo del request.

**`services/pagatu-orden-ms/src/main/java/pe/edu/upeu/orden/controller/OrdenController.java`** — el método `crear`:

```java
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenResponse crear(@Valid @RequestBody OrdenRequest request,
                                @AuthenticationPrincipal Jwt jwt) {
        Number claimIdCliente = jwt.getClaim("idCliente");
        if (claimIdCliente == null) {
            throw new IllegalArgumentException("Token sin idCliente: solo un CLIENTE autenticado puede crear ordenes");
        }
        return ordenService.crear(request, claimIdCliente.longValue());
    }
```

Agrega los imports:

```java
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
```

`@AuthenticationPrincipal Jwt jwt` entrega el token **ya verificado** (3.19) — Spring Security lo dejó en el contexto de seguridad de la petición, así que no hay ningún filtro propio que escribir ni ninguna librería JWT que agregar. El claim se lee como `Number` (no como `Long` directo) porque el JSON no distingue entre enteros de distinto tamaño.

**`services/pagatu-orden-ms/src/main/java/pe/edu/upeu/orden/service/OrdenService.java`** — actualiza la firma:

```java
OrdenResponse crear(OrdenRequest request, Long idCliente);
```

**`services/pagatu-orden-ms/src/main/java/pe/edu/upeu/orden/service/OrdenServiceImpl.java`** — el método `crear`, recibiendo `idCliente` aparte en vez de leerlo de `request`:

```java
    @Override
    @Transactional
    public OrdenResponse crear(OrdenRequest request, Long idCliente) {
        Orden orden = Orden.builder()
                .idCliente(idCliente)
                .metodoPago(request.getMetodoPago())
                .build();

        // el resto del método sigue exactamente igual que en S6
```

Solo cambian la firma del método y esa primera línea del `builder()` — el resto de `crear()` (validación de productos vía Feign, cálculo del total, `toResponse()`) no tiene ninguna relación con `idCliente` y queda intacto.

`IllegalArgumentException` ya cae en el `GlobalExceptionHandler` que `pagatu-orden-ms` trae desde S1/S6 — confirma que responde con un código de error claro (no `500`) antes de continuar; si tu manejador actual no cubre `IllegalArgumentException`, agrégale un `@ExceptionHandler` que devuelva `400 Bad Request`.

#### 3.21 Probar de punta a punta, autenticado como `CLIENTE`

**Producto del paso:** una orden creada con `idCliente` tomado del JWT — nunca escrito a mano.

Reinicia `pagatu-orden-ms` (para que lea la configuración nueva). Con `pagatu-config`, `pagatu-eureka`, `pagatu-gateway`, `pagatu-auth-ms`, `pagatu-catalogo-ms` y `pagatu-orden-ms` corriendo, crea una orden **a través del Gateway**, con el token de `cliente@pagatu.com` (3.12):

PowerShell:

```powershell
$tokenCliente = "PEGA_AQUI_EL_TOKEN_DE_CLIENTE_DE_3.12"

Invoke-RestMethod -Method Post -Uri "http://localhost:18080/api/v1/ordenes" `
  -Headers @{ Authorization = "Bearer $tokenCliente" } `
  -ContentType "application/json" `
  -Body '{"metodoPago": "YAPE_PLIN", "detalles": [{"idProducto": 1, "cantidad": 2}]}'
```

bash macOS/Linux:

```bash
TOKEN_CLIENTE="PEGA_AQUI_EL_TOKEN_DE_CLIENTE_DE_3.12"

curl -X POST http://localhost:18080/api/v1/ordenes \
  -H "Authorization: Bearer $TOKEN_CLIENTE" \
  -H "Content-Type: application/json" \
  -d '{"metodoPago": "YAPE_PLIN", "detalles": [{"idProducto": 1, "cantidad": 2}]}'
```

Resultado esperado — `201 Created`, con `"idCliente": 1` **aunque el request nunca lo mencionó**:

```json
{
  "id": 2,
  "idCliente": 1,
  "fechaCreacion": "2026-09-20T10:15:00",
  "estado": "PENDIENTE_PAGO",
  "total": 200.0,
  "detalles": [
    { "idProducto": 1, "nombreProducto": "...", "cantidad": 2, "precioUnitario": 100.0, "subtotal": 200.0 }
  ]
}
```

**Ese `idCliente: 1` en la respuesta es la prueba de que hoy funcionó** — vino del claim `idCliente` del JWT (3.3, el usuario semilla `cliente@pagatu.com` tiene `id_cliente: 1`), no de nada que el request haya escrito.

Repite el mismo request sin el header `Authorization` y confirma `401` (ya no llega ni a `pagatu-orden-ms`, el Gateway lo rechaza primero, 3.17). Repite con el token de `admin@pagatu.com`: también pasa el Gateway (la regla de 3.15 permite `CLIENTE` **o** `ADMIN`) y el token es válido en `pagatu-orden-ms`, pero no trae `idCliente` — el usuario `ADMIN` semilla no tiene `id_cliente` asignado (3.3), así que tu `IllegalArgumentException` (3.20) debería dispararse aquí y devolver `400`. Si eso pasa, es el comportamiento esperado, no un bug — confirma que un `ADMIN` sin `idCliente` no puede crear una orden a nombre de nadie.

#### 3.22 Probar el llamado directo a `pagatu-orden-ms`, sin pasar por el Gateway

**Producto del paso:** evidencia concreta de por qué `pagatu-orden-ms` valida el token por su cuenta, en vez de confiar en que "ya pasó por el Gateway".

Llama directo al puerto de `pagatu-orden-ms` (`8082`), **sin** el Gateway de por medio, con un token manipulado a mano:

PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8082/api/v1/ordenes" `
  -Headers @{ Authorization = "Bearer token.invalido.manipulado" } `
  -ContentType "application/json" `
  -Body '{"metodoPago": "YAPE_PLIN", "detalles": [{"idProducto": 1, "cantidad": 2}]}'
```

bash macOS/Linux:

```bash
curl -i -X POST http://localhost:8082/api/v1/ordenes \
  -H "Authorization: Bearer token.invalido.manipulado" \
  -H "Content-Type: application/json" \
  -d '{"metodoPago": "YAPE_PLIN", "detalles": [{"idProducto": 1, "cantidad": 2}]}'
```

Resultado esperado: `401 Unauthorized`, con un encabezado `WWW-Authenticate: Bearer error="invalid_token"` — `pagatu-orden-ms` verificó la firma por sí mismo, falló, y rechazó la petición **sin llegar al controlador**. Repite sin ningún header `Authorization`: también `401`. Y repite con el token **válido** de `cliente@pagatu.com`: `201 Created` — lo que autoriza la petición es el **token**, no el camino por el que llegó.

**En DEV este escenario es alcanzable** (`8082` sigue expuesto al host, S6); en producción local (S4) esta ruta directa ni siquiera existe — `pagatu-orden-ms` no publica ningún puerto. Pero una defensa que solo funciona si nadie se salta la primera línea no es una defensa: por eso la segunda línea existe aunque hoy "no haga falta".

#### 3.23 Documentar la matriz de roles y accesos

**Producto del paso:** contrato de seguridad documentado — igual que S6 documentó el contrato de un evento, esta sesión documenta el contrato de acceso.

**Tabla 8. Matriz de roles y accesos verificada en 3.17 y 3.21**

| Ruta | Método | Rol requerido | Sin token | Token `ADMIN` | Token `CLIENTE` |
|---|---|---|---|---|---|
| `/api/v1/auth/login` | POST | público | `200` | `200` | `200` |
| `/api/v1/productos` | GET | público | `200` | `200` | `200` |
| `/api/v1/productos` | POST | `ADMIN` | `401` | `201` | `403` |
| `/api/v1/ordenes` | POST | `CLIENTE` o `ADMIN` (`idCliente` requerido) | `401` | `400` (sin `idCliente`) | `201` |
| `/api/v1/ordenes` | GET | `CLIENTE` o `ADMIN` | `401` | `200` | `200` |

### Parte D — Verificar y entender lo que viene

#### 3.24 Revisar los accesos y revocar un rol

**Producto del paso:** una revisión de accesos (*access review*) hecha sobre el sistema real, la prueba de que quitar un rol revoca el acceso — pero solo en el **siguiente** login —, y la lista de verificación de errores comunes (2.3, 2.4) completada.

**Revisión de accesos.** Consulta quién tiene qué rol directamente en la base de datos de `pagatu-auth-ms`:

```bash
docker exec -it pagatu-postgres-auth-dev psql -U pagatu -d pagatu_auth_db -c "SELECT u.email, r.nombre AS rol FROM usuarios u JOIN usuario_roles ur ON ur.usuario_id = u.id JOIN roles r ON r.id = ur.rol_id ORDER BY u.email, r.nombre;"
```

Resultado esperado — dos filas, una por usuario semilla:

```text
       email        |   rol
--------------------+---------
 admin@pagatu.com   | ADMIN
 cliente@pagatu.com | CLIENTE
(2 rows)
```

Esta consulta es una revisión de accesos mínima: en una sola mirada dice quién puede qué. En un sistema real se repite de forma periódica y se revoca lo que ya no corresponde.

**Revocar un rol.** Simula que `cliente@pagatu.com` deja de ser cliente. Ten a la mano el token de `cliente@pagatu.com` de 3.12 (sin haber reiniciado `pagatu-auth-ms` desde entonces, 3.7) y quítale el rol:

```bash
docker exec -it pagatu-postgres-auth-dev psql -U pagatu -d pagatu_auth_db -c "DELETE FROM usuario_roles WHERE usuario_id = (SELECT id FROM usuarios WHERE email = 'cliente@pagatu.com');"
```

Ahora comprueba dos cosas, ambas a través del Gateway (`POST /api/v1/ordenes`, como en 3.21):

1. Con el **token viejo** (el de antes de revocar): sigue devolviendo `201 Created`. El JWT es *stateless* (2.2): el token ya emitido lleva el rol **adentro** y sigue siendo válido hasta que expire — quitar el rol de la base de datos no lo invalida.
2. Pide un **token nuevo** (login de `cliente@pagatu.com`, 3.12) y pégalo en [jwt.io](https://jwt.io): el claim ahora es `"realm_access": { "roles": [] }`. Con ese token, `POST /api/v1/ordenes` devuelve `403 Forbidden` — autenticado, pero sin ningún rol que alcance.

Es exactamente la limitación de 2.2 (Error frecuente), vista con datos reales: revocar un rol es inmediato para logins **nuevos**, pero los tokens vigentes viven hasta su expiración. Por eso `jwt.expiracion-segundos` debe ser corto en un sistema real.

**Deja el sistema como estaba** antes de continuar:

```bash
docker exec -it pagatu-postgres-auth-dev psql -U pagatu -d pagatu_auth_db -c "INSERT INTO usuario_roles (usuario_id, rol_id) SELECT u.id, r.id FROM usuarios u, roles r WHERE u.email = 'cliente@pagatu.com' AND r.nombre = 'CLIENTE';"
```

Repite la consulta de revisión y confirma que vuelven a aparecer las dos filas.

**Lista de verificación.** Compara el sistema de hoy contra los errores comunes de 2.3 y 2.4:

**Tabla 9. Lista de verificación de accesos: errores comunes frente al sistema de hoy**

| Error común | Cómo se comprueba en `pagatu` | Resultado esperado hoy |
|---|---|---|
| Permisos asignados directo a usuarios, en vez de a roles | Revisa `usuario_roles` (3.3) y las reglas de 3.15: nada se autoriza por usuario | No ocurre: todo pasa por rol. |
| Roles demasiado amplios | Revisa la Tabla 8: `ADMIN` puede llamar a `/api/v1/ordenes` (3.15), aunque sin `idCliente` termina en `400` (3.21) | Ocurre en parte: decide si esa ruta debería aceptar solo `CLIENTE`. |
| Sin revisión periódica de accesos | La consulta de esta sección | Se hace a mano, una vez; en un sistema real sería periódica. |
| Sin registro de auditoría de los rechazos | Log de `pagatu-gateway` con nivel de seguridad en `DEBUG` (2.6) | Solo queda en consola; un *audit log* real iría a un almacén aparte. |
| Flujo *Implicit* | Ningún flujo de hoy usa redirects | No ocurre. |
| `state` sin validar | No hay redirects hoy | No aplica hoy; sí en S11. |
| Tokens en `localStorage` | El único *client* de hoy es PowerShell | No aplica hoy; es un riesgo real en S11. |
| *Refresh token* sin rotación | `pagatu-auth-ms` no emite *refresh token* | No aplica hoy; Keycloak sí lo emite. |
| *Authorization Code* sin PKCE en cliente público | No hay clientes públicos hoy | No aplica hoy; en S11 se usa PKCE. |
| Usuario y contraseña enviados directo al servidor (*Resource Owner Password*) | El `POST /api/v1/auth/login` (3.8) | **Ocurre, a propósito:** es la pieza temporal que Keycloak reemplaza (2.4). |

#### 3.25 Ver PKCE en acción

**Producto del paso:** un `code_verifier` y su `code_challenge` generados por ti, y la comprobación de por qué interceptar el `code` no alcanza (2.4, Figura 6).

Este paso no toca ningún servicio: reproduce el cálculo que hará el cliente Angular en S11. Genera un `code_verifier` aleatorio, calcula su `code_challenge` (SHA-256, en Base64 apto para URL) y calcula el `code_challenge` de un valor **casi igual** — el mismo verificador con un carácter más.

PowerShell:

```powershell
function Get-Pkce([string]$verifier) {
    $hash = [System.Security.Cryptography.SHA256]::Create().ComputeHash([System.Text.Encoding]::ASCII.GetBytes($verifier))
    [Convert]::ToBase64String($hash).TrimEnd('=').Replace('+','-').Replace('/','_')
}

$bytes = New-Object byte[] 48
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$codeVerifier = [Convert]::ToBase64String($bytes).TrimEnd('=').Replace('+','-').Replace('/','_')

"code_verifier:                  $codeVerifier"
"code_challenge:                 $(Get-Pkce $codeVerifier)"
"challenge con un carácter más:  $(Get-Pkce ($codeVerifier + 'x'))"
```

bash macOS/Linux:

```bash
pkce() { printf '%s' "$1" | openssl dgst -sha256 -binary | openssl base64 -A | tr '+/' '-_' | tr -d '='; echo; }

CODE_VERIFIER=$(openssl rand -base64 48 | tr -d '=+/\n' | cut -c1-64)

echo "code_verifier:                 $CODE_VERIFIER"
echo "code_challenge:                $(pkce "$CODE_VERIFIER")"
echo "challenge con un carácter más: $(pkce "${CODE_VERIFIER}x")"
```

Resultado esperado: tres líneas. El `code_challenge` tiene 43 caracteres, y el `challenge con un carácter más` **no se parece en nada** al anterior — un solo carácter distinto cambia el hash completo.

Ahora conecta lo que ves con la Figura 6: en el paso 1, el cliente envía **solo** el `code_challenge`; en el paso 4, presenta el `code_verifier` original, y el servidor calcula el hash y comprueba que coincide. Quien intercepte el `code` en el paso 3 solo conoce el `code_challenge` — y como el hash no se puede invertir, no puede reconstruir el `code_verifier` que hace falta para canjear ese `code`. Por eso PKCE protege a los clientes que no pueden guardar un `client_secret` (RFC 7636).

**Evidencia de aprendizaje:**

- `pagatu-auth-ms` operativo en DEV, con usuarios y roles en sus propias tablas, registrado en `pagatu-eureka` y con configuración externa desde `pagatu-config`.
- Login exitoso devolviendo un `access_token` firmado (RS256), y login fallido devolviendo `401`.
- `GET /.well-known/jwks.json` publicando solo la clave pública.
- `pagatu-gateway` como Resource Server, con los cuatro casos de 3.17: sin token (`401`), token alterado (`401`), rol incorrecto (`403`) y rol correcto (`201`/`200`).
- `pagatu-orden-ms` como Resource Server: orden creada con `idCliente` tomado del JWT, y llamado directo con token alterado rechazado con `401`.
- Matriz de roles y accesos (Tabla 8) y lista de verificación (Tabla 9) completadas.

## 4. Crea: actividad autónoma

Tiempo: 4h fuera del aula.

### 4.1 Actividad

Protección de `pagatu-cliente-ms` (construido de forma autónoma desde S2) con el mismo patrón de roles aplicado hoy a `pagatu-catalogo-ms` y `pagatu-orden-ms`, documentada en evidencia individual.

Completa y evidencia estas tareas:

1. Definir qué rutas de `pagatu-cliente-ms` necesitan qué rol (por ejemplo: consultar el propio perfil exige estar autenticado; listar todos los clientes exige `ADMIN`) y agregarlas a `SecurityConfig` de `pagatu-gateway` (mismo patrón de 3.15).
2. Agregar la ruta de `pagatu-cliente-ms` al Gateway (mismo patrón de 3.16).
3. Convertir `pagatu-cliente-ms` en Resource Server, para que valide el JWT por su cuenta (mismo patrón de 3.19).
4. Probar el caso permitido y el caso denegado a través del Gateway, y un llamado directo al puerto de `pagatu-cliente-ms` con un token alterado, con capturas de los códigos de respuesta (mismo patrón de 3.17 y 3.22).
5. Documentar la matriz de roles y accesos de `pagatu-cliente-ms`, mismo formato de la Tabla 8 (3.23).
6. Registrar aporte individual.

### 4.2 Propósito

Que cada estudiante demuestre, de forma individual y fuera del aula, que puede extender el patrón de control de acceso centralizado a un tercer microservicio de negocio sin el acompañamiento del docente — la misma habilidad que el proyecto necesita para proteger cualquier microservicio nuevo que aparezca en U3.

Esta actividad autónoma se desarrolla sobre el proyecto de fin de curso del equipo. El producto de la unidad se construye por acumulación de los avances de cada sesión; por eso, la evidencia de esta sesión debe incorporarse a la documentación del proyecto y quedar trazable en GitHub.

### 4.3 Indicaciones

Entrega un PDF con el siguiente nombre:

```text
S07_Equipo##_ApellidoNombre.pdf
```

Cada captura de pantalla del informe debe mostrar, sin recortar, el reloj del sistema (fecha y hora) y tu usuario o foto de perfil (Windows, VS Code o navegador) visibles en pantalla — es lo que permite verificar que la evidencia es tuya y que corresponde al momento real de tu trabajo.

#### 4.3.1 Estructura del informe

**Datos del estudiante**

- Nombre:
- Equipo:
- Sesión: S07 - Seguridad distribuida y control de acceso
- Rol o aporte realizado:
- Link de GitHub:

**Evidencia técnica**

Incluye capturas o extractos con una breve explicación debajo de cada uno, organizados en los mismos 4 bloques de la rúbrica (4.6):

1. *`pagatu-auth-ms` construido*
    - Captura del login exitoso devolviendo un JWT, del login fallido devolviendo `401`, y de las tablas `usuarios`, `roles` y `usuario_roles` con sus datos (trabajo de clase).
2. *`pagatu-gateway` como Resource Server*
    - Captura de los cuatro casos de 3.17: sin token (`401`), token alterado (`401`), rol incorrecto (`403`), rol correcto (`201`/`200`).
3. *`pagatu-orden-ms` valida el JWT y toma `idCliente` de él*
    - Captura de la orden creada en 3.21, mostrando `idCliente` en la respuesta sin que el request lo haya declarado, y del llamado directo de 3.22 rechazado con `401`.
4. *`pagatu-cliente-ms` protegido*
    - Caso permitido y caso denegado a través del Gateway, llamado directo con token alterado rechazado, y la matriz de roles y accesos documentada (trabajo autónomo).

**Error o hallazgo**

Describe un error real: un `jwk-set-uri` con el puerto o la ruta incorrectos (`401` en todo), un `pagatu-auth-ms` reiniciado después de pedir el token (la firma dejó de verificar), un `JwtAuthenticationConverter` que no leía `realm_access.roles` (ningún `hasRole` funcionaba), o un `idCliente` que llegaba `null` porque el header `Authorization` no se copió completo.

**Reflexión técnica breve**

Responde en 5 a 8 líneas:

```text
Si mañana reemplazas pagatu-auth-ms por Keycloak, ¿qué parte del Gateway y
de pagatu-orden-ms sigue igual, cuál cambia, y por qué esa diferencia es la
razón de haber elegido una firma asimétrica (clave privada / clave pública)
en vez de un secreto compartido?
```

**Anexo: Feedback de la sesión**

Pega esta página como la última hoja del PDF, con tus respuestas.

1. ¿Cuál es el aprendizaje más importante que te llevas de la clase de hoy?
2. ¿Qué punto de la clase te resultó más confuso o te dejó con dudas?
3. ¿Tienes alguna pregunta que te gustaría que sea respondida la siguiente clase?
4. Sobre tu nivel de comprensión de la clase de hoy, marca una opción:
    - ¡Entendido! - Lo domino y podría explicarlo.
    - Más o menos. - Entendí la idea general, pero tengo dudas.
    - Necesito ayuda. - Me siento perdido/a con este tema.
5. ¿Cómo puedo ayudarte a comprender mejor el tema?
6. Pensando en tu participación y esfuerzo en la clase de hoy, ¿cómo te autoevaluarías? Marca una opción:
    - Muy Comprometido/a: Me esforcé al máximo.
    - Comprometido/a: Sé que podría haberme esforzado un poco más.
    - Poco Comprometido/a: Hoy no di mi mejor esfuerzo.
7. Mi satisfacción con la clase fue... (califica del 1 al 10, donde 1 es insatisfecho y 10 es muy satisfecho).

### 4.4 Criterios mínimos de aceptación

- PDF con nombre correcto.
- `pagatu-auth-ms` evidenciado: login exitoso con JWT, login fallido con `401`, roles en su propia tabla.
- Evidencia de los cuatro casos de acceso a través del Gateway (sin token, token alterado, rol incorrecto, rol correcto).
- Evidencia de `pagatu-orden-ms` creando una orden con `idCliente` tomado del JWT, no del request, y rechazando con `401` un llamado directo con token alterado.
- `pagatu-cliente-ms` protegido con el mismo patrón (Gateway y Resource Server propio), con matriz de roles y accesos documentada.
- Aporte individual verificable.

### 4.5 Preguntas de defensa

1. ¿Por qué el mensaje de error de un login fallido no distingue entre "el email no existe" y "la contraseña es incorrecta"?
2. ¿Por qué el Gateway y `pagatu-orden-ms` pueden verificar un token sin guardar ningún secreto, y qué ganas con eso frente a un secreto compartido?
3. ¿Qué pasa si `pagatu-auth-ms` está caído, o se reinició, cuando llega el primer token al Gateway?
4. ¿Qué diferencia hay entre que el Gateway rechace una petición por falta de token (`401`) y que la rechace por rol incorrecto (`403`)?
5. Si `pagatu-orden-ms` se llama directo, sin pasar por el Gateway, ¿qué lo protege de un token inválido?
6. ¿Qué cambia y qué no cambia en el Gateway y en `pagatu-orden-ms` el día que `pagatu-auth-ms` se reemplace por Keycloak?
7. ¿Qué problema resuelve PKCE que un `client_secret` no puede resolver en una aplicación que corre en el navegador?
8. Si quitas un rol a un usuario en la base de datos, ¿por qué su token ya emitido sigue funcionando, y cómo se limita ese riesgo?

### 4.6 Rúbrica de evaluación

| Dimensión | Peso | 3 - Logro destacado | 2 - Logro | 1 - Proceso | 0 - Inicio | Puntuación obtenida |
|---|---:|---|---|---|---|---:|
| 1. `pagatu-auth-ms` construido | 2 | Login completo: JWT firmado, clave pública publicada, roles en tabla propia, error controlado con `401`, usuarios semilla con BCrypt. | Login funcional con partes menores incompletas. | Login parcial. | No evidencia `pagatu-auth-ms` funcionando. | |
| 2. `pagatu-gateway` como Resource Server | 2 | Evidencia los cuatro casos (`401` sin token, `401` token alterado, `403`, éxito) con capturas claras. | Evidencia funcional con algún caso incompleto. | Evidencia parcial o poco clara. | No evidencia protección del Gateway. | |
| 3. `pagatu-orden-ms` valida el JWT y toma `idCliente` de él | 2 | `idCliente` tomado del JWT ya verificado, llamado directo rechazado con `401`, evidenciado y explicado. | Funcional, evidencia parcial. | Cambio incompleto o inconsistente. | Sigue aceptando `idCliente` del request. | |
| 4. `pagatu-cliente-ms` protegido | 1 | Roles definidos con criterio, Resource Server propio, casos permitido/denegado y llamado directo evidenciados, matriz documentada. | Protección funcional, matriz incompleta. | Protección parcial. | No evidencia protección de `pagatu-cliente-ms`. | |
| 5. Matriz de roles y accesos | 1 | Matriz completa y verificada contra evidencia real. | Matriz completa, sin verificación clara. | Matriz incompleta. | No documenta la matriz. | |
| 6. Aporte individual | 1 | Aporte claro y verificable. | Aporte identificable. | Aporte general. | No se identifica aporte. | |
| 7. Orden y reflexión | 1 | PDF ordenado y reflexión técnica clara. | Evidencia suficiente. | Evidencia poco clara. | PDF insuficiente. | |

Puntuación acumulada = suma de (`Peso` × `Puntuación obtenida`) = ____.

Nota final = (`Puntuación acumulada` / 30) × 20 = ____.

Para usar la rúbrica con IA, solicita:

```text
Evalúa el PDF usando la rúbrica de la sesión.
Para cada dimensión selecciona la puntuación obtenida usando la escala Inicio=0, Proceso=1, Logro=2, Logro destacado=3.
Justifica brevemente cada puntuación.
Calcula la puntuación acumulada con la fórmula: suma de (Peso × Puntuación obtenida).
Calcula la nota final sobre 20 con la fórmula: (Puntuación acumulada / 30) × 20.
Indica 2 fortalezas y 2 recomendaciones.
```

## 5. Cierre

Tiempo: 5 min.

**Resumen breve:** hoy el sistema ganó su tercer microservicio (`pagatu-auth-ms`) y su primera capa de seguridad real: `pagatu-auth-ms` firma los tokens con una clave privada que nunca sale de él, `pagatu-gateway` pasó de ser solo un punto único de acceso a ser la primera línea de validación de identidad y rol, y `pagatu-orden-ms` dejó de confiar en un dato que cualquiera podía inventar (`idCliente`) para tomarlo de un JWT que verifica por su cuenta y que nadie puede falsificar sin la clave privada. Nada de eso depende de que `pagatu-auth-ms` sea quien emite el token: cuando llegue Keycloak, cambia una propiedad de configuración.

**Dinámica participativa:** en una ronda rápida, cada estudiante comparte en una frase qué código de respuesta obtuvo al intentar modificar el catálogo con un token de `CLIENTE` — y por qué ese código (`403`, no `401`) es la respuesta correcta.

**Metacognición:** ¿qué parte de la sesión te costó más entender — que el JWT no depende de ningún estado guardado en el servidor (stateless), o que `pagatu-orden-ms` vuelve a verificar la firma aunque el Gateway ya la haya validado?

**Proyección:** S8 agrega mensajería asíncrona entre servicios desacoplados, con Kafka — `pagatu-orden-ms`, ya protegido hoy, publicará `orden.creada` con el `idCliente` real (tomado del JWT, no inventado) hacia un microservicio de pagos que todavía no existe, y consumirá el resultado del pago (`pago.validado`; en S9, `pago.fallido` para compensar). S11 conecta un cliente Angular real, que va a necesitar guardar este mismo JWT y mandarlo en cada petición — nada de lo construido hoy en el Gateway ni en `pagatu-orden-ms` queda obsoleto, es la base exacta que ese cliente real va a consumir. Lo único temporal es `pagatu-auth-ms`: cuando el proyecto integre Keycloak, el cliente dejará de escribir su contraseña en un formulario propio y pasará al flujo *Authorization Code con PKCE* (2.4), pero los *resource servers* de hoy seguirán validando el mismo tipo de token.

## Bibliografía

- Denniss, W., & Bradley, J. (2019). *OAuth 2.0 Device Authorization Grant* (RFC 8628). IETF. https://datatracker.ietf.org/doc/html/rfc8628
- Hardt, D. (Ed.). (2012). *The OAuth 2.0 Authorization Framework* (RFC 6749). IETF. https://datatracker.ietf.org/doc/html/rfc6749
- Jones, M. (2015). *JSON Web Key (JWK)* (RFC 7517). IETF. https://datatracker.ietf.org/doc/html/rfc7517
- Jones, M., Bradley, J., & Sakimura, N. (2015). *JSON Web Token (JWT)* (RFC 7519). IETF. https://datatracker.ietf.org/doc/html/rfc7519
- JWT.io / Auth0. (2024). *Introduction to JSON Web Tokens*. https://jwt.io/introduction
- Keycloak. (2025). *Keycloak Documentation*. https://www.keycloak.org/documentation
- Lodderstedt, T., Bradley, J., Labunets, A., & Fett, D. (2025). *Best Current Practice for OAuth 2.0 Security* (RFC 9700). IETF. https://datatracker.ietf.org/doc/html/rfc9700
- SACAViX. (s. f.-a). *RBAC / Control de Acceso* [Patrón de diseño]. System Design. https://systemdesign.sacavix.com/patterns
- SACAViX. (s. f.-b). *OAuth2 / OpenID Connect* [Patrón de diseño]. System Design. https://systemdesign.sacavix.com/patterns
- Sakimura, N., Bradley, J., & Agarwal, N. (2015). *Proof Key for Code Exchange by OAuth Public Clients* (RFC 7636). IETF. https://datatracker.ietf.org/doc/html/rfc7636
- Sakimura, N., Bradley, J., Jones, M., de Medeiros, B., & Mortimore, C. (2014). *OpenID Connect Core 1.0*. OpenID Foundation. https://openid.net/specs/openid-connect-core-1_0.html
- Spring Team. (2024). *Spring Security Reference: OAuth2 Resource Server*. https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
