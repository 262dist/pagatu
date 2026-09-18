# S7 - Seguridad distribuida y control de acceso

*Por: Angel Sullon Macalupu @asullom - 2026*

## 1. Introducción

Tiempo: 20 min.

### 1.1 Presentación de la sesión

Hasta S6, `pagatu-orden-ms` confía en el `idCliente` que el propio request declara — el DTO `OrdenRequest` lo trae como un campo más, igual que `metodoPago`. Nada impide que cualquiera, con Swagger o una simple petición, escriba `"idCliente": 1` y cree una orden a nombre de otra persona: el sistema nunca pregunta *quién eres*, solo confía en lo que el request dice ser. Esta sesión cierra esa puerta: aparece `pagatu-auth-ms`, un microservicio nuevo que autentica usuarios y emite un JWT firmado; `pagatu-gateway` pasa a exigir y validar ese JWT antes de dejar pasar cualquier petición (Resource Server); y `pagatu-orden-ms` deja de aceptar `idCliente` en el request — lo toma directo del JWT que el Gateway ya validó.

### 1.2 Índice

1. Autenticación stateless con JWT.
2. Autorización basada en roles.
3. El Gateway como punto único de validación (Resource Server).
4. Observabilidad y diagnóstico.

### 1.3 Propósito de aprendizaje

Al concluir la clase, estarás en condiciones de:

- **Implementar** autenticación y autorización distribuida con JWT y Spring Security, centralizando la validación en el Gateway como Resource Server y protegiendo rutas del sistema por rol, con evidencia real de accesos permitidos y denegados.

### 1.4 Producto de sesión

`pagatu-auth-ms` funcional — con usuarios semilla, login que emite un JWT firmado, conectado a Config Server y a Eureka —; `pagatu-gateway` protegido como Resource Server, con rutas restringidas por rol (`ADMIN`/`CLIENTE`); y `pagatu-orden-ms` tomando `idCliente` directo del JWT ya validado, no del request.

### 1.5 Metodología

**Tabla 1. Metodología de la sesión**

| Actividades a Realizar en el Periodo | Orientaciones generales (Orientaciones Metodológicas) | Material de estudio recomendado |
|---|---|---|
| Revisión previa individual | Confirmar que `pagatu-config`, `pagatu-eureka`, `pagatu-gateway`, `pagatu-catalogo-ms` y `pagatu-orden-ms` (S1-S6) siguen arrancando en DEV. Revisar el `OrdenRequest` actual de `pagatu-orden-ms` (S6, 3.5) y confirmar que `idCliente` hoy es un campo libre del request. Trabajo individual, antes de clase. | Evidencia individual de S6, [Alcance por microservicio y proyecto base](../proyecto-sello/alcance-microservicios.md). |
| Clase presencial | Construcción guiada de `pagatu-auth-ms` de punta a punta, protección de `pagatu-gateway` como Resource Server, y migración de `pagatu-orden-ms` para tomar `idCliente` del JWT. Trabajo individual, siguiendo al docente paso a paso; consulta inmediata ante un `401`/`403` inesperado. | Pasos 3.1 a 3.23 de esta guía. |
| Evaluación formativa | Revisión en clase de la matriz de accesos (3.23): login exitoso, acceso denegado sin token, acceso denegado por rol incorrecto, y `pagatu-orden-ms` creando una orden con el `idCliente` tomado del JWT. La evidencia se completa y sustenta de forma individual, fuera del aula, según los criterios mínimos de la sección 4.4. | Indicaciones de entrega (4.3), rúbrica de evaluación (4.6). |

### 1.6 Motivación de la sesión

#### 1.6.1 Caso: la orden que se creó a nombre de otra persona

Un compañero de equipo revisa `pagatu-orden-ms` con Swagger, un día antes de la sustentación de S5. Prueba el endpoint `POST /api/v1/ordenes` con el cuerpo de ejemplo que ya trae la documentación — y sin querer, sin ninguna credencial, crea una orden real con `idCliente: 1`. El sistema la acepta sin preguntar nada: ni quién hizo la petición, ni si esa persona tiene permiso de actuar en nombre del cliente `1`. `idCliente` es solo un número más dentro de un JSON, tan editable como `metodoPago`.

El problema no es que alguien haya probado el endpoint — es que el sistema nunca definió *quién puede decir que es quién*. Cualquier dato que el propio cliente declara sobre sí mismo (su identidad, su rol) no es un dato confiable: hay que verificarlo contra algo que el cliente no controla. Un JWT firmado por un servicio de confianza (`pagatu-auth-ms`) es exactamente eso — el cliente no puede fabricar uno válido sin conocer el secreto con el que se firma, así que cualquier claim dentro de un JWT válido (incluido `idCliente`) sí es confiable.

**Preguntas de análisis**

**Activación de conocimientos previos**

1. ¿Por qué un campo `idCliente` dentro del cuerpo de un request HTTP no es, por sí solo, una prueba de identidad?
2. Si `pagatu-gateway` ya rechaza peticiones sin token válido, ¿por qué `pagatu-orden-ms` necesita además leer el JWT — no le alcanza con que el Gateway ya lo validó?

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
    ClienteAngular["Cliente real<br/>Angular 21+ (S11)"]
    Gateway["pagatu-gateway<br/>construido en S4<br/>HOY: Resource Server (JWT)"]
    Auth["pagatu-auth-ms<br/>HOY<br/>login, emite JWT"]
    Orden["pagatu-orden-ms<br/>construido en S6<br/>HOY: idCliente desde el JWT"]
    Catalogo["pagatu-catalogo-ms<br/>construido en S1"]
    Cliente["pagatu-cliente-ms<br/>construido en S2 (autónomo)<br/>autónomo hoy: mismos roles"]
    Eureka["pagatu-eureka<br/>construido en S3"]
    Config["pagatu-config<br/>construido en S2"]
    Kafka[("Kafka<br/>candidato, futuro (S8)")]
    Pago["pago-ms<br/>candidato, futuro (S8)"]
    Obs[("Observabilidad<br/>futuro, S10")]

    ClientePrueba --> Gateway
    ClienteAngular --> Gateway
    Gateway -->|"lb://pagatu-auth-ms"| Auth
    Gateway -->|"lb://pagatu-orden-ms"| Orden
    Gateway -->|"lb://pagatu-catalogo-ms"| Catalogo
    Gateway -->|"lb://pagatu-cliente-ms"| Cliente
    Orden -->|"Feign"| Catalogo
    Auth -. "registra<br/>instancia" .-> Eureka
    Gateway -. "descubre<br/>servicios" .-> Eureka
    Auth -. "carga<br/>configuración" .-> Config
    Orden -.->|"orden.creada"| Kafka
    Kafka -.->|"consume"| Pago
    Gateway -. "logs y métricas" .-> Obs
    Auth -. "logs y métricas" .-> Obs

    classDef done fill:#e8f5e9,stroke:#2e7d32,color:#111;
    classDef today fill:#ffe08a,stroke:#9a6b00,stroke-width:2px,color:#111;
    classDef futuro fill:#f5f5f5,stroke:#9e9e9e,color:#555,stroke-dasharray: 5 5;
    class Catalogo,Eureka,Config done;
    class Auth,Gateway,Orden today;
    class Cliente today;
    class Kafka,Pago,Obs,ClienteAngular futuro;
```

Hoy aparece `pagatu-auth-ms`, el tercer microservicio del proyecto, y dos piezas ya existentes ganan una capa nueva: `pagatu-gateway` (S4) pasa a validar JWT antes de enrutar cualquier petición, y `pagatu-orden-ms` (S6) deja de confiar en el `idCliente` que el request declara. `pagatu-cliente-ms` (construido de forma autónoma desde S2) queda protegido con el mismo patrón de roles como trabajo autónomo de hoy (sección 4) — el mismo patrón de seguridad, aplicado sobre un tercer microservicio de negocio, igual que S6 aplicó el mismo patrón de construcción a `pagatu-cliente-ms` como su propio trabajo autónomo.

`Kafka` y `pago-ms` (S8), Saga (S9) y observabilidad completa (S10) todavía no existen — se muestran porque ya están agendados en el sílabo de esta unidad, no porque se esté adelantando trabajo.

## 2. Explica

Tiempo: 30 min.

### 2.1 Arquitectura de la sesión

**Figura 2. `pagatu-auth-ms` emite el JWT; `pagatu-gateway` lo valida; `pagatu-orden-ms` lee sus claims**

```mermaid
flowchart TB
    Cliente["Cliente<br/>PowerShell / bash / Swagger"]

    subgraph Paso1["1. Autenticación"]
        direction LR
        Auth["pagatu-auth-ms<br/>POST /api/v1/auth/login"]
        AuthDB[("pagatu_auth_db")]
        Auth --> AuthDB
    end

    subgraph Paso2["2. Autorización centralizada"]
        direction LR
        GW["pagatu-gateway<br/>Resource Server<br/>valida firma + rol"]
    end

    subgraph Paso3["3. Consumo del claim ya validado"]
        direction LR
        Orden["pagatu-orden-ms<br/>lee idCliente del JWT"]
        Catalogo["pagatu-catalogo-ms<br/>lee rol del JWT"]
    end

    Cliente -->|"1. email + password"| Auth
    Auth -->|"JWT firmado<br/>claims: sub, rol, idCliente"| Cliente
    Cliente -->|"2. Authorization: Bearer JWT"| GW
    GW -->|"3a. reenvía el mismo header"| Orden
    GW -->|"3b. reenvía el mismo header"| Catalogo

    style Paso1 fill:transparent,stroke:transparent
    style Paso2 fill:transparent,stroke:transparent
    style Paso3 fill:transparent,stroke:transparent
```

Tres pasos, tres responsabilidades que no se mezclan: `pagatu-auth-ms` (Paso 1) solo confirma credenciales y firma el JWT — no sabe nada de órdenes ni de productos. `pagatu-gateway` (Paso 2) es el **único** lugar donde se valida la firma del JWT y se decide si el rol alcanza para esa ruta — ningún microservicio de negocio repite esa validación. `pagatu-orden-ms` (Paso 3) confía en el JWT sin volver a verificar su firma, porque en producción local (S4) es **imposible** llegar a `pagatu-orden-ms` sin pasar antes por el Gateway — ningún microservicio publica su puerto al host salvo el Gateway (S4, 2.2). En DEV esa garantía no existe (`pagatu-orden-ms` sigue escuchando directo en `8082`) — 3.22 muestra qué pasa si se lo llama directo, sin pasar por el Gateway.

### 2.2 Autenticación stateless con JWT

Un sistema tradicional con sesiones guarda, en el servidor, quién está autenticado — una cookie de sesión es solo una referencia a ese estado guardado. Eso no encaja con un sistema distribuido: si la sesión vive en la memoria de una instancia de `pagatu-gateway`, y el balanceador (S4) manda la siguiente petición a otra instancia, esa instancia no tiene idea de que el usuario ya se autenticó.

Un **JWT** (*JSON Web Token*) resuelve esto sin guardar nada en el servidor: es un token que lleva **dentro de sí mismo** toda la información necesaria (sus *claims* — email, rol, `idCliente`), firmado con una clave que solo conoce el sistema que lo emitió. Cualquier instancia de `pagatu-gateway` puede validar la firma sin consultarle nada a `pagatu-auth-ms` ni a ninguna base de datos — por eso es **stateless**: el propio token es la prueba, no una referencia a un estado guardado en otro lado.

**Tabla 2. Autenticación con sesión vs. autenticación con JWT**

| | Con sesión (stateful) | Con JWT (stateless) |
|---|---|---|
| Dónde vive el estado de "quién está autenticado" | En el servidor (memoria o BD de sesiones) | Dentro del propio token, firmado |
| Qué necesita cada instancia para validar | Consultar el mismo almacén de sesiones que las demás | Nada más que la clave de firma — no depende de las demás instancias |
| Qué pasa si el balanceador manda la petición a otra instancia | Falla, salvo que las instancias compartan el almacén de sesiones | Funciona igual: cualquier instancia valida el mismo token |
| Cómo se revoca antes de que expire | Borrando la sesión del almacén | No es trivial (2.2, Error frecuente) |

**Error frecuente**: asumir que un JWT se puede "cerrar sesión" como una sesión tradicional. Como el token no depende de ningún estado en el servidor, invalidarlo antes de su expiración natural exige un mecanismo aparte (una lista negra de tokens revocados, por ejemplo) — fuera del alcance de esta sesión. Por eso `jwt.expiracion-ms` (3.10) debe ser un valor corto en un sistema real; en esta sesión se deja largo (una hora) solo para no complicar las pruebas manuales.

### 2.3 Autorización basada en roles

Autenticación responde *quién eres*; autorización responde *qué puedes hacer* — son dos preguntas distintas, y un JWT válido solo resuelve la primera. Un `CLIENTE` autenticado con un JWT perfectamente válido no debería poder borrar un producto del catálogo — ese JWT prueba su identidad, no le da permiso para esa acción.

Esta sesión usa **RBAC** (*Role-Based Access Control*): cada usuario tiene un rol (`ADMIN` o `CLIENTE`, 3.3) empaquetado como *claim* dentro del JWT, y cada ruta del Gateway declara qué rol necesita (3.15) — `hasRole("ADMIN")` en vez de una lista de usuarios autorizados uno por uno, que no escalaría a medida que el sistema crece.

### 2.4 Observabilidad y diagnóstico

Cuando una petición falla con `401` o `403`, el problema puede estar en tres lugares distintos, y diagnosticarlo bien depende de saber cuál: el JWT no se envió o está mal formado (revisa el header `Authorization` que realmente salió del cliente), el JWT es válido pero expiró o la firma no coincide (revisa que `jwt.secret` sea **exactamente** el mismo en `pagatu-auth-ms` y en `pagatu-gateway` — 3.10), o el JWT es válido pero el rol no alcanza para esa ruta (revisa la regla de `SecurityConfig`, 3.15, contra el rol real del usuario). Los logs de `pagatu-gateway` (3.17) y el `traceId` de cada petición (mismo `CorrelationIdFilter` de S1/S6, si ya lo replicaste en `pagatu-auth-ms`) son el punto de partida para distinguir estos tres casos sin adivinar.

## 3. Aplica: actividad práctica guiada

Tiempo: 4h.

**Actividad:** construcción guiada de `pagatu-auth-ms`, protección de `pagatu-gateway` como Resource Server, y migración de `pagatu-orden-ms` para tomar `idCliente` del JWT ya validado (Producto de la sesión en 1.4).

**Propósito de la actividad:** que cada estudiante implemente autenticación stateless con JWT y autorización basada en roles, centralizando la validación en un único punto (el Gateway) y verificando con evidencia real accesos permitidos y denegados — no solo el caso feliz.

**Orientaciones metodológicas:** en el laboratorio, el docente construye las tres partes de la sesión en orden frente a la clase — primero `pagatu-auth-ms` completo (Parte A), después la protección del Gateway (Parte B), al final la migración de `pagatu-orden-ms` (Parte C) —; los estudiantes replican cada paso en su propio equipo, y provocan ellos mismos los casos denegados (3.17, 3.22) para ver el `401`/`403` real en su propia consola, no solo leer el resultado esperado en la guía.

**Actividades para realizar:**

*Parte A — Construir `pagatu-auth-ms`:*

- **3.1** Crear el proyecto base de `pagatu-auth-ms`.
- **3.2** Levantar la base de datos de `pagatu-auth-ms`.
- **3.3** Crear la migración Flyway con los usuarios semilla.
- **3.4** Crear la entidad `Usuario` y el enum `Rol`.
- **3.5** Crear los DTO de login.
- **3.6** Crear el repositorio y el servicio de JWT.
- **3.7** Crear el servicio de autenticación y el controlador.
- **3.8** Configurar Spring Security en `pagatu-auth-ms`.
- **3.9** Conectar `pagatu-auth-ms` a `pagatu-config` y a `pagatu-eureka`.
- **3.10** Compartir el secreto JWT en `config-repo`.
- **3.11** Levantar y probar `pagatu-auth-ms` de punta a punta.

*Parte B — Proteger `pagatu-gateway` como Resource Server:*

- **3.12** Agregar la dependencia de OAuth2 Resource Server.
- **3.13** Configurar el decodificador JWT y el conversor de roles.
- **3.14** Proteger las rutas del Gateway por rol.
- **3.15** Agregar la ruta de `pagatu-auth-ms` al Gateway.
- **3.16** Probar accesos permitidos y denegados a través del Gateway.

*Parte C — `pagatu-orden-ms` toma el `idCliente` del JWT:*

- **3.17** Quitar `idCliente` del DTO de entrada.
- **3.18** Agregar la dependencia de JJWT.
- **3.19** Crear el filtro que extrae los claims del JWT.
- **3.20** Actualizar el controlador y el servicio de `pagatu-orden-ms`.
- **3.21** Probar de punta a punta, autenticado como `CLIENTE`.
- **3.22** Probar el llamado directo a `pagatu-orden-ms`, sin pasar por el Gateway.
- **3.23** Documentar la matriz de roles y accesos.

**Punto de partida común:** todo el equipo debe comenzar exactamente desde donde quedó S6 (Feign y Circuit Breaker), no desde su propio avance individual. Clona la rama `s06-orden-ms-circuit-breaker`:

```bash
git clone --branch s06-orden-ms-circuit-breaker https://github.com/262dist/pagatu.git
```

Levanta en DEV los servicios base ya construidos hasta S6 (`pagatu-config`, `pagatu-eureka`, `pagatu-gateway`, `pagatu-catalogo-ms`, `pagatu-orden-ms`) antes de tocar código nuevo — si alguno falla en arrancar, el problema es de una sesión anterior, no de esta.

### Parte A — Construir `pagatu-auth-ms`

#### 3.1 Crear el proyecto base de `pagatu-auth-ms`

**Producto del paso:** proyecto `pagatu-auth-ms` creado, con las mismas dependencias base que `pagatu-orden-ms` (S6) más Spring Security.

**Tabla 3. Configuración de `pagatu-auth-ms` en Spring Initializr**

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

Agrega también a mano, en el `pom.xml`, **JJWT** — Spring Initializr no lo ofrece como opción (mismo criterio que MapStruct en S1, 3.5.20):

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

`jjwt-api` es la única que el código importa directamente (`Jwts`, `Claims`); `jjwt-impl` y `jjwt-jackson` solo hacen falta en tiempo de ejecución (implementación real y serialización JSON de los claims) — por eso van con `scope: runtime`, mismo criterio que ya usa `postgresql` desde S1.

El puerto de base de datos (`15431` DEV / `25431` PROD local) y el nombre `pagatu_auth_db` ya estaban reservados desde la arquitectura del proyecto (`docs/index.md`) — no se inventan en esta sesión. El puerto de aplicación en DEV es `8084` — el siguiente libre después de `8080`/`8081` (`pagatu-catalogo-ms`, S1/S3) y `8082`/`8083` (`pagatu-orden-ms`, S6).

Si Spring Initializr todavía no ofrece **Spring Boot 4** como opción, agrega Spring Security a mano en el `pom.xml` después de generar el proyecto: `<artifactId>spring-boot-starter-security</artifactId>`, sin `<version>` (la gestiona el padre `spring-boot-starter-parent`). Revisa también, igual que en S6, que ningún starter conserve un nombre de Boot 3 (por ejemplo `spring-boot-starter-web` en vez de `spring-boot-starter-webmvc`).

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

#### 3.3 Crear la migración Flyway con los usuarios semilla

**Producto del paso:** tabla `usuarios` creada, con dos usuarios de prueba — uno `ADMIN`, uno `CLIENTE`.

**`services/pagatu-auth-ms/src/main/resources/db/migration/V1__create_usuario_table.sql`:**

```sql
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    id_cliente BIGINT,
    PRIMARY KEY (id)
);

INSERT INTO usuarios (email, password, rol, id_cliente) VALUES
    ('admin@pagatu.com', '$2b$10$ndX5v/xbbP8LAFlts57QweeqsmxNNDTkWZG4wpmShADMaVTON9bfC', 'ADMIN', NULL),
    ('cliente@pagatu.com', '$2b$10$zCONDt0UNbJJh4C936JZyubU7ceojmoItVznixqKnivxjuxsofAPC', 'CLIENTE', 1);
```

Las contraseñas ya están hasheadas con BCrypt (nunca se guarda una contraseña en texto plano, ni siquiera en datos semilla de práctica): el usuario `admin@pagatu.com` tiene contraseña real `admin123`, y `cliente@pagatu.com` tiene `cliente123` — verificados de antemano contra esos dos hashes exactos. `id_cliente: 1` en el usuario `CLIENTE` es el mismo `idCliente` que S6 usaba a mano en el request (3.20 lo reemplaza por este valor, tomado del JWT en vez de escrito por quien llama).

**Si necesitas generar tu propio hash** (por ejemplo, para el trabajo autónomo de `pagatu-cliente-ms`, 4.1), agrega temporalmente este endpoint, pruébalo una vez, y **bórralo antes de entregar** — exponer un generador de hashes en un endpoint público es un riesgo de seguridad, no algo que quede en el proyecto final:

```java
@GetMapping("/api/v1/auth/_hash-temporal")
public String hashTemporal(@RequestParam String password, PasswordEncoder encoder) {
    return encoder.encode(password);
}
```

#### 3.4 Crear la entidad `Usuario` y el enum `Rol`

**Producto del paso:** mapeo JPA de la tabla `usuarios`.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/entity/Rol.java`:**

```java
package pe.edu.upeu.auth.entity;

public enum Rol {
    ADMIN,
    CLIENTE
}
```

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/entity/Usuario.java`:**

```java
package pe.edu.upeu.auth.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(name = "id_cliente")
    private Long idCliente;
}
```

`idCliente` es `null` para un usuario `ADMIN` (no representa a ningún cliente) y tiene valor para un usuario `CLIENTE` — ese valor es el que viaja dentro del JWT y el que `pagatu-orden-ms` va a leer en la Parte C, en vez de confiar en el que el request declare.

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

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo;
    private long expiraEnMs;
}
```

`tipo` siempre vale `"Bearer"` (3.7) — así el cliente sabe exactamente cómo debe mandar el token de vuelta: `Authorization: Bearer <token>`.

#### 3.6 Crear el repositorio y el servicio de JWT

**Producto del paso:** acceso a `usuarios`, y la pieza que sabe firmar y emitir un JWT.

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

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/service/JwtService.java`:**

```java
package pe.edu.upeu.auth.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Getter
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiracion-ms}")
    private long expiracionMs;

    public String generarToken(String email, String rol, Long idCliente) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expiracionMs);

        JwtBuilder builder = Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(expira);

        if (idCliente != null) {
            builder = builder.claim("idCliente", idCliente);
        }

        return builder.signWith(key).compact();
    }
}
```

`Keys.hmacShaKeyFor(...)` exige una clave de al menos 256 bits (32 caracteres) para HS256 — `jwt.secret` (3.10) tiene que respetar ese mínimo, o `pagatu-auth-ms` falla al arrancar con `WeakKeyException`. `idCliente` solo se agrega como claim cuando no es `null` (un `ADMIN` no lo necesita) — así el JWT de un `ADMIN` no lleva un claim vacío sin sentido.

#### 3.7 Crear el servicio de autenticación y el controlador

**Producto del paso:** `POST /api/v1/auth/login` funcional, verificando credenciales contra `usuarios` y devolviendo un JWT.

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

import pe.edu.upeu.auth.dto.LoginRequest;
import pe.edu.upeu.auth.dto.LoginResponse;
import pe.edu.upeu.auth.entity.Usuario;
import pe.edu.upeu.auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new BadCredentialsException("Credenciales invalidas");
        }

        String token = jwtService.generarToken(usuario.getEmail(), usuario.getRol().name(), usuario.getIdCliente());

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .expiraEnMs(jwtService.getExpiracionMs())
                .build();
    }
}
```

El mensaje de error es **el mismo** (`"Credenciales invalidas"`) tanto si el email no existe como si la contraseña no coincide — revelar cuál de las dos cosas falló le regala información a quien intenta adivinar credenciales ajenas (confirmaría qué emails sí están registrados).

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/exception/GlobalExceptionHandler.java`:**

```java
package pe.edu.upeu.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", ex.getMessage()));
    }
}
```

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/controller/AuthController.java`:**

```java
package pe.edu.upeu.auth.controller;

import pe.edu.upeu.auth.dto.LoginRequest;
import pe.edu.upeu.auth.dto.LoginResponse;
import pe.edu.upeu.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

#### 3.8 Configurar Spring Security en `pagatu-auth-ms`

**Producto del paso:** `/api/v1/auth/**` accesible sin autenticación previa (tiene sentido: nadie tiene un JWT todavía antes de hacer login), y el `PasswordEncoder` disponible para inyectar.

**`services/pagatu-auth-ms/src/main/java/pe/edu/upeu/auth/config/SecurityConfig.java`:**

```java
package pe.edu.upeu.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());
        return http.build();
    }
}
```

**Error frecuente**: agregar `spring-boot-starter-security` al `pom.xml` y no declarar ningún `SecurityFilterChain` propio. Spring Security se autoconfigura por defecto en cuanto detecta la dependencia — bloquea todo con un formulario de login y una contraseña generada al azar (visible en el log de arranque), en vez de dejar pasar libremente `/api/v1/auth/login`. Este `SecurityFilterChain` explícito reemplaza esa configuración por defecto.

#### 3.9 Conectar `pagatu-auth-ms` a `pagatu-config` y a `pagatu-eureka`

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

Agrega el `pom.xml` las mismas dos dependencias de Spring Cloud que ya usa `pagatu-orden-ms` (S3, S6) — sin ellas, `pagatu-auth-ms` ni externaliza configuración ni se registra en Eureka:

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

#### 3.10 Compartir el secreto JWT en `config-repo`

**Producto del paso:** un único `jwt.secret`, compartido por `pagatu-auth-ms`, `pagatu-gateway` (Parte B) y `pagatu-orden-ms` (Parte C) — sin copiarlo a mano en cada uno.

`config-repo` (S2) hoy solo tiene archivos `{spring.application.name}-{perfil}.yml`, uno por servicio. Spring Cloud Config Server también reconoce un archivo especial: **`application.yml`** (sin nombre de servicio) — se lo sirve a **todos** los clientes que le pregunten, sea cual sea su `spring.application.name`, mezclado con (y sobrescrito por) el archivo específico de cada uno. Es el lugar correcto para algo que varios servicios necesitan **igual**, como un secreto de firma compartido.

**`infra/pagatu-config/config-repo/application.yml`:**

```yaml
jwt:
  secret: "pagatu-jwt-secret-super-larga-cambiar-en-produccion-real-2026"
```

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
  expiracion-ms: 3600000
```

Mismo patrón exacto de `pagatu-orden-ms-dev.yml` (S6) — `ddl-auto: validate` porque el esquema real lo define Flyway (3.3), no Hibernate; `jwt.expiracion-ms: 3600000` son 60 minutos, deliberadamente largo solo para no complicar las pruebas manuales de hoy (2.2, Error frecuente). `jwt.secret` **no** se repite aquí: ya llega desde `application.yml` (el archivo global de arriba), y este archivo específico no necesita sobrescribirlo.

**Verifica** que el Config Server sirve ambos archivos correctamente antes de continuar:

PowerShell:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:18888/pagatu-auth-ms/dev"
```

bash macOS/Linux:

```bash
curl http://localhost:18888/pagatu-auth-ms/dev
```

**Si `propertySources` sale sin ningún `jwt.secret`, no continúes.** Confirma que `application.yml` (el global, sin nombre de servicio) existe exactamente en `config-repo/` — no dentro de una subcarpeta, ni con un nombre distinto — y que el archivo se guardó y `pagatu-config` lo recargó (reinicia `pagatu-config` si hace falta).

#### 3.11 Levantar y probar `pagatu-auth-ms` de punta a punta

**Producto del paso:** login real, con un JWT devuelto y verificable.

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
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBwYWdhdHUuY29tIiwicm9sIjoiQURNSU4iLCJpYXQiOjE3ODk2OTk...",
  "tipo": "Bearer",
  "expiraEnMs": 3600000
}
```

Prueba también con una contraseña incorrecta y confirma `401 Unauthorized` con `{"error": "Credenciales invalidas"}` — el manejador de 3.7 en acción. Guarda el token del `ADMIN` y repite el login con `cliente@pagatu.com` / `cliente123` — vas a necesitar **ambos** tokens en 3.16 y 3.21.

**(Opcional) ¿Solo quieres inspeccionar un JWT sin escribir código?** Pega el token en [jwt.io](https://jwt.io) — el *payload* decodificado debe mostrar `sub`, `rol` y (para el token de `cliente@pagatu.com`) `idCliente: 1`. jwt.io no valida la firma contra tu `jwt.secret` a menos que se lo pegues también; úsalo solo para **leer** los claims, nunca pegues ahí un secreto o un token de un sistema real.

### Parte B — Proteger `pagatu-gateway` como Resource Server

#### 3.12 Agregar la dependencia de OAuth2 Resource Server

**Producto del paso:** `pagatu-gateway` con capacidad de validar JWT.

**`infra/pagatu-gateway/pom.xml`:**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

Sin versión explícita — la gestiona `spring-boot-starter-parent`. Esta dependencia sola ya trae todo lo necesario para decodificar y validar un JWT (no hace falta agregar `spring-boot-starter-security` aparte, viene incluida de forma transitiva).

#### 3.13 Configurar el decodificador JWT y el conversor de roles

**Producto del paso:** `pagatu-gateway` capaz de verificar la firma de un JWT con el mismo secreto de `pagatu-auth-ms`, y de traducir el claim `rol` a un rol de Spring Security.

**`infra/pagatu-gateway/src/main/java/pe/edu/upeu/gateway/config/SecurityConfig.java`** (primera mitad — decodificador y conversor):

```java
package pe.edu.upeu.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String rol = jwt.getClaimAsString("rol");
            Collection<GrantedAuthority> authorities =
                    rol == null ? List.of() : List.of(new SimpleGrantedAuthority("ROLE_" + rol));
            return authorities;
        });
        return converter;
    }
    // continúa en 3.14
}
```

`NimbusJwtDecoder.withSecretKey(...)` valida la firma HMAC del JWT con la **misma clave** que `pagatu-auth-ms` usó para firmarlo (3.6) — si `jwt.secret` no coincide letra por letra entre los dos servicios, todo JWT válido de `pagatu-auth-ms` va a fallar la verificación acá, con un `401` genérico difícil de diagnosticar sin este dato (2.4). El `JwtAuthenticationConverter` traduce el claim `rol` (un string simple: `"ADMIN"` o `"CLIENTE"`) al formato que Spring Security espera para autorizar por rol (`ROLE_ADMIN`, `ROLE_CLIENTE`) — sin este conversor, `hasRole("ADMIN")` (3.14) nunca encontraría ninguna autoridad que coincida, porque Spring Security por defecto busca el claim `scope`, no `rol`.

#### 3.14 Proteger las rutas del Gateway por rol

**Producto del paso:** reglas de autorización reales — quién puede hacer qué, por ruta y por método HTTP.

Completa el mismo archivo de 3.13, agregando el `SecurityFilterChain`:

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

#### 3.15 Agregar la ruta de `pagatu-auth-ms` al Gateway

**Producto del paso:** el Gateway sabe enrutar `/api/v1/auth/**` hacia `pagatu-auth-ms`.

**`infra/pagatu-config/config-repo/pagatu-gateway-dev.yml`** — agrega esta ruta a la lista que ya existe desde S4 (no reemplaces las otras):

```yaml
            - id: pagatu-auth-login
              uri: lb://pagatu-auth-ms
              predicates:
                - Path=/api/v1/auth/**
```

Recuerda agregar también, en `pagatu-gateway-dev.yml`, el mismo `jwt.secret` — espera, **no hace falta**: ya llega desde `application.yml` global (3.10), y `pagatu-gateway` es otro cliente más del mismo `pagatu-config`.

#### 3.16 Probar accesos permitidos y denegados a través del Gateway

**Producto del paso:** evidencia real de los tres casos — sin token, con rol incorrecto, con rol correcto.

Levanta `pagatu-gateway`:

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

**Caso 2 — token de `CLIENTE`, intentando modificar el catálogo (rol incorrecto):**

PowerShell:

```powershell
$tokenCliente = "PEGA_AQUI_EL_TOKEN_DE_CLIENTE_DE_3.11"

Invoke-RestMethod -Method Post -Uri "http://localhost:18080/api/v1/productos" `
  -Headers @{ Authorization = "Bearer $tokenCliente" } `
  -ContentType "application/json" `
  -Body '{"nombre": "Producto de prueba", "precio": 10.0}'
```

bash macOS/Linux:

```bash
TOKEN_CLIENTE="PEGA_AQUI_EL_TOKEN_DE_CLIENTE_DE_3.11"

curl -i -X POST http://localhost:18080/api/v1/productos \
  -H "Authorization: Bearer $TOKEN_CLIENTE" \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Producto de prueba", "precio": 10.0}'
```

Resultado esperado: `403 Forbidden` — el token es válido (pasó la firma), pero el rol `CLIENTE` no alcanza para `hasRole("ADMIN")`.

**Caso 3 — token de `ADMIN`, mismo endpoint (rol correcto):**

Repite el Caso 2 con el token de `admin@pagatu.com`. Resultado esperado: `201 Created` (o el código que ya devuelva `pagatu-catalogo-ms` al crear un producto).

**Error frecuente**: copiar el token con comillas o espacios de más al pegarlo en la variable — el header queda mal formado y Spring Security lo rechaza como si no hubiera token, un `401` que en realidad es un error de copiado, no de configuración.

### Parte C — `pagatu-orden-ms` toma el `idCliente` del JWT

#### 3.17 Quitar `idCliente` del DTO de entrada

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

Este es exactamente el cambio que 1.6.1 pedía: `idCliente` ya no es un dato que el cliente declara sobre sí mismo — 3.20 lo reemplaza por un valor que viene del JWT, imposible de falsificar sin conocer `jwt.secret`.

#### 3.18 Agregar la dependencia de JJWT

**Producto del paso:** `pagatu-orden-ms` capaz de leer (no de emitir) un JWT.

**`services/pagatu-orden-ms/pom.xml`** — mismas tres dependencias de 3.1:

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

`pagatu-orden-ms` **no** agrega `spring-boot-starter-security` ni `spring-boot-starter-oauth2-resource-server` — esa validación completa (firma + rol) ya la hace `pagatu-gateway` (Parte B), y en producción local (S4) es el único camino posible hacia `pagatu-orden-ms`. Acá solo hace falta **leer** un claim de un JWT que ya se asume válido — un filtro liviano (3.19), no una segunda cadena completa de Spring Security duplicando el trabajo del Gateway.

#### 3.19 Crear el filtro que extrae los claims del JWT

**Producto del paso:** `idCliente` y `rol` disponibles como atributos del request, listos para que el controlador los use.

**`services/pagatu-orden-ms/src/main/java/pe/edu/upeu/orden/filter/JwtClaimsFilter.java`:**

```java
package pe.edu.upeu.orden.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtClaimsFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

                Object idCliente = claims.get("idCliente");
                if (idCliente != null) {
                    request.setAttribute("idCliente", ((Number) idCliente).longValue());
                }
                request.setAttribute("rol", claims.get("rol"));
            } catch (Exception ex) {
                logger.warn("Token invalido en pagatu-orden-ms: " + ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

`@Component` sobre un `OncePerRequestFilter` alcanza para que Spring Boot lo registre solo, como filtro de servlet — no hace falta configurarlo a mano en ningún lado. **Sí vuelve a verificar la firma** del JWT (no es una confianza ciega): es una verificación barata, y cubre el caso de una prueba directa contra `pagatu-orden-ms` sin pasar por el Gateway (3.22) — si esa verificación falla, o no hay token, simplemente no se completa el atributo `idCliente`, y el controlador (3.20) responde con un error claro en vez de una `NullPointerException`.

#### 3.20 Actualizar el controlador y el servicio de `pagatu-orden-ms`

**Producto del paso:** `crear()` recibe `idCliente` como parámetro aparte, nunca del cuerpo del request.

**`services/pagatu-orden-ms/src/main/java/pe/edu/upeu/orden/controller/OrdenController.java`** — el método `crear`:

```java
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrdenResponse crear(@Valid @RequestBody OrdenRequest request,
                                @RequestAttribute(value = "idCliente", required = false) Long idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("Token sin idCliente: solo un CLIENTE autenticado puede crear ordenes");
        }
        return ordenService.crear(request, idCliente);
    }
```

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

Con `pagatu-config`, `pagatu-eureka`, `pagatu-gateway`, `pagatu-auth-ms`, `pagatu-catalogo-ms` y `pagatu-orden-ms` corriendo, crea una orden **a través del Gateway**, con el token de `cliente@pagatu.com` (3.11):

PowerShell:

```powershell
$tokenCliente = "PEGA_AQUI_EL_TOKEN_DE_CLIENTE_DE_3.11"

Invoke-RestMethod -Method Post -Uri "http://localhost:18080/api/v1/ordenes" `
  -Headers @{ Authorization = "Bearer $tokenCliente" } `
  -ContentType "application/json" `
  -Body '{"metodoPago": "YAPE_PLIN", "detalles": [{"idProducto": 1, "cantidad": 2}]}'
```

bash macOS/Linux:

```bash
TOKEN_CLIENTE="PEGA_AQUI_EL_TOKEN_DE_CLIENTE_DE_3.11"

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

Repite el mismo request sin el header `Authorization` y confirma `401` (ya no llega ni a `pagatu-orden-ms`, el Gateway lo rechaza primero, 3.16). Repite con el token de `admin@pagatu.com`: también debería crear la orden (la regla de 3.14 permite `CLIENTE` **o** `ADMIN`), pero con `idCliente: null` — el usuario `ADMIN` semilla no tiene `id_cliente` asignado (3.3), así que tu `IllegalArgumentException` (3.20) debería dispararse acá. Si eso pasa, es el comportamiento esperado, no un bug — confirma que un `ADMIN` sin `idCliente` no puede crear una orden a nombre de nadie.

#### 3.22 Probar el llamado directo a `pagatu-orden-ms`, sin pasar por el Gateway

**Producto del paso:** evidencia concreta de por qué el filtro de 3.19 vuelve a verificar la firma, en vez de confiar ciegamente en que "ya pasó por el Gateway".

Llama directo al puerto de `pagatu-orden-ms` (`8082`), **sin** el Gateway de por medio, con un token cualquiera manipulado a mano (cámbiale un carácter al final):

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

Resultado esperado: `400 Bad Request` (tu `IllegalArgumentException` de 3.20) — el filtro de 3.19 intentó verificar la firma, falló, no completó `idCliente`, y el controlador rechazó la petición. **En DEV esto es alcanzable** (`8082` sigue expuesto al host, S6); en producción local (S4) esta ruta directa ni siquiera existe — `pagatu-orden-ms` no publica ningún puerto, así que este escenario solo es posible mientras se corre con Maven en el host.

### 3.23 Documentar la matriz de roles y accesos

**Producto del paso:** contrato de seguridad documentado — igual que S6 documentó el contrato de un evento, esta sesión documenta el contrato de acceso.

**Tabla 4. Matriz de roles y accesos verificada en 3.16 y 3.21**

| Ruta | Método | Rol requerido | Sin token | Token `ADMIN` | Token `CLIENTE` |
|---|---|---|---|---|---|
| `/api/v1/auth/login` | POST | público | `200` | `200` | `200` |
| `/api/v1/productos` | GET | público | `200` | `200` | `200` |
| `/api/v1/productos` | POST | `ADMIN` | `401` | `201` | `403` |
| `/api/v1/ordenes` | POST | `CLIENTE` o `ADMIN` (`idCliente` requerido) | `401` | `400` (sin `idCliente`) | `201` |
| `/api/v1/ordenes` | GET | autenticado | `401` | `200` | `200` |

## 4. Crea: actividad autónoma

Tiempo: 4h fuera del aula.

### 4.1 Actividad

Protección de `pagatu-cliente-ms` (construido de forma autónoma desde S2) con el mismo patrón de roles aplicado hoy a `pagatu-catalogo-ms` y `pagatu-orden-ms`, documentada en evidencia individual.

Completa y evidencia estas tareas:

1. Definir qué rutas de `pagatu-cliente-ms` necesitan qué rol (por ejemplo: consultar el propio perfil exige estar autenticado; listar todos los clientes exige `ADMIN`) y agregarlas a `SecurityConfig` de `pagatu-gateway` (mismo patrón de 3.14).
2. Agregar la ruta de `pagatu-cliente-ms` al Gateway (mismo patrón de 3.15).
3. Probar el caso permitido y el caso denegado, con capturas de ambos códigos de respuesta (mismo patrón de 3.16).
4. Documentar la matriz de roles y accesos de `pagatu-cliente-ms`, mismo formato de la Tabla 4 (3.23).
5. Registrar aporte individual.

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
    - Captura del login exitoso devolviendo un JWT, y del login fallido devolviendo `401` (trabajo de clase).
2. *`pagatu-gateway` como Resource Server*
    - Captura de los tres casos de 3.16: sin token (`401`), rol incorrecto (`403`), rol correcto (`201`/`200`).
3. *`pagatu-orden-ms` toma `idCliente` del JWT*
    - Captura de la orden creada en 3.21, mostrando `idCliente` en la respuesta sin que el request lo haya declarado.
4. *`pagatu-cliente-ms` protegido*
    - Caso permitido y caso denegado, con la matriz de roles y accesos documentada (trabajo autónomo).

**Error o hallazgo**

Describe un error real: un `jwt.secret` que no coincidía letra por letra entre `pagatu-auth-ms` y `pagatu-gateway` (firma inválida, `401` en todo), un `JwtAuthenticationConverter` sin registrar (ningún `hasRole` funcionaba), o un `idCliente` que llegaba `null` porque el header `Authorization` no se copió completo.

**Reflexión técnica breve**

Responde en 5 a 8 líneas:

```text
¿Por qué validar el JWT una sola vez en el Gateway, en vez de repetir esa
misma validación completa en cada microservicio, no significa que los
microservicios "confíen ciegamente" en cualquier petición que les llega?
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
- `pagatu-auth-ms` evidenciado: login exitoso con JWT, login fallido con `401`.
- Evidencia de los tres casos de acceso a través del Gateway (sin token, rol incorrecto, rol correcto).
- Evidencia de `pagatu-orden-ms` creando una orden con `idCliente` tomado del JWT, no del request.
- `pagatu-cliente-ms` protegido con el mismo patrón, con matriz de roles y accesos documentada.
- Aporte individual verificable.

### 4.5 Preguntas de defensa

1. ¿Por qué el mensaje de error de un login fallido no distingue entre "el email no existe" y "la contraseña es incorrecta"?
2. ¿Qué pasa si `jwt.secret` es distinto en `pagatu-auth-ms` y en `pagatu-gateway`?
3. ¿Por qué `pagatu-orden-ms` no necesita `spring-boot-starter-security` completo para leer el `idCliente` del JWT?
4. ¿Qué diferencia hay entre que el Gateway rechace una petición por falta de token (`401`) y que la rechace por rol incorrecto (`403`)?
5. Si `pagatu-orden-ms` se llama directo, sin pasar por el Gateway, ¿qué lo protege igual de un token inválido?

### 4.6 Rúbrica de evaluación

| Dimensión | Peso | 3 - Logro destacado | 2 - Logro | 1 - Proceso | 0 - Inicio | Puntuación obtenida |
|---|---:|---|---|---|---|---:|
| 1. `pagatu-auth-ms` construido | 2 | Login completo: JWT válido, error controlado con `401`, usuarios semilla con BCrypt. | Login funcional con partes menores incompletas. | Login parcial. | No evidencia `pagatu-auth-ms` funcionando. | |
| 2. `pagatu-gateway` como Resource Server | 2 | Evidencia los tres casos (`401`/`403`/éxito) con capturas claras. | Evidencia funcional con algún caso incompleto. | Evidencia parcial o poco clara. | No evidencia protección del Gateway. | |
| 3. `pagatu-orden-ms` toma `idCliente` del JWT | 2 | `idCliente` correctamente tomado del JWT, evidenciado y explicado. | Funcional, evidencia parcial. | Cambio incompleto o inconsistente. | Sigue aceptando `idCliente` del request. | |
| 4. `pagatu-cliente-ms` protegido | 1 | Roles definidos con criterio, casos permitido/denegado evidenciados, matriz documentada. | Protección funcional, matriz incompleta. | Protección parcial. | No evidencia protección de `pagatu-cliente-ms`. | |
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

**Resumen breve:** hoy el sistema ganó su tercer microservicio (`pagatu-auth-ms`) y su primera capa de seguridad real: `pagatu-gateway` pasó de ser solo un punto único de acceso a ser también el único punto de validación de identidad y rol, y `pagatu-orden-ms` dejó de confiar en un dato que cualquiera podía inventar (`idCliente`) para tomarlo de un JWT que nadie puede falsificar sin conocer el secreto de firma.

**Dinámica participativa:** en una ronda rápida, cada estudiante comparte en una frase qué código de respuesta obtuvo al intentar modificar el catálogo con un token de `CLIENTE` — y por qué ese código (`403`, no `401`) es la respuesta correcta.

**Metacognición:** ¿qué parte de la sesión te costó más entender — que el JWT no depende de ningún estado guardado en el servidor (stateless), o que `pagatu-orden-ms` vuelve a verificar la firma aunque el Gateway ya la haya validado?

**Proyección:** S8 agrega mensajería asíncrona entre servicios desacoplados, con Kafka — `pagatu-orden-ms`, ya protegido hoy, publicará `orden.creada` con el `idCliente` real (tomado del JWT, no inventado) hacia un microservicio de pagos que todavía no existe. S11 conecta un cliente Angular real, que va a necesitar guardar este mismo JWT y mandarlo en cada petición — nada de lo construido hoy queda obsoleto, es la base exacta que ese cliente real va a consumir.

## Bibliografía

- Spring Team. (2024). *Spring Security Reference: OAuth2 Resource Server*. https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html
- JWT.io / Auth0. (2024). *Introduction to JSON Web Tokens*. https://jwt.io/introduction
- Java JWT (jjwt). (2024). *JJWT Documentation*. https://github.com/jwtk/jjwt
