# Propuesta de Migración: ProyectosMS2026 a Arquitectura Pagatu

Este documento detalla el análisis de la arquitectura anterior (`ProyectosMS2026`) y establece la estrategia técnica para migrar los componentes útiles hacia el nuevo estándar unificado de **Pagatu**.

---

## 1. DIAGNÓSTICO DE LA ARQUITECTURA ANTERIOR (`ProyectosMS2026`)

### 1.1. Patrones Reutilizables
*   **Trazabilidad Distribuida (Correlación)**:
    *   `TraceIdGlobalFilter` (en el API Gateway) que genera e inyecta la cabecera HTTP `X-Trace-ID`.
    *   `CorrelationIdFilter` (en cada microservicio) que extrae dicha cabecera e inicializa el `MDC` (Mapped Diagnostic Context) de Logback para asegurar la trazabilidad transversal en todos los hilos de logs.
    *   `FeignTraceConfig` que inyecta automáticamente el identificador de traza en las peticiones síncronas salientes realizadas mediante clientes OpenFeign.
*   **Seguridad JWT Descentralizada**:
    *   Mecanismo sin estado (Stateless) donde `auth-ms` valida credenciales en base de datos local y emite tokens firmados con HMAC-SHA256, y el Gateway actúa como un Resource Server reactivo validando digitalmente las firmas.
*   **Consumo Resiliente en Kafka**:
    *   Uso de `ErrorHandlingDeserializer` y `CommonErrorHandler` con políticas de retroceso (`FixedBackOff`) en `pago-ms` para neutralizar fallos catastróficos por mensajes corruptos (*poison pills*).

### 1.2. Código Obsoleto
*   **Microservicio `producto` independiente**:
    *   En la nueva arquitectura de Pagatu, el microservicio `producto` queda obsoleto como módulo separado. Sus responsabilidades de inventario e ítems se fusionarán directamente con `catalogo-ms` para simplificar la topología y evitar comunicaciones de red innecesarias para obtener categorías.
*   **`GatewayInstanciasController`**:
    *   Controlador de depuración presente en `producto` y `catalogo` que expone la lista de IPs físicas registradas en Eureka. Debe eliminarse por motivos de seguridad informática.
*   **Excepciones de seguridad permisivas en Gateway**:
    *   Mapeos temporales en `SecurityConfig` que permiten peticiones anónimas hacia recursos core (`/api/v1/categorias/**`, etc.).

### 1.3. Puertos Fijos Detectados
Puertos configurados de forma rígida en la arquitectura anterior:

| Componente | Perfil | Puerto Interno | Puerto Host Expuesto |
|---|---|---|---|
| **Config Server** | prod (Docker) | `7071` | `7072` |
| **Registry (Eureka)** | prod (Docker) | `7081` | `7082` |
| **Gateway** | prod (Docker) | `7091` | `7092` |
| **Auth MS** | dev / prod | `8041` / `8042` | `8041` / `8042` |
| **Catalogo MS** | dev / prod | `8081` / `8082` | `8081` / `8082` |
| **Producto MS** | dev / prod | `9091` / `9092` | `9091` / `9092` |
| **Orden MS** | dev / prod | `19051` / `9021` | `19051` / `29051` |
| **Pago MS** | dev / prod | `19061` / `9031` | `19061` / `29061` |
| **Kafka Broker** | prod (Docker) | `9092` | `29092` |
| **Kafka UI** | prod (Docker) | `8080` | `28085` |
| **Grafana** | prod (Docker) | `3000` | `23000` |
| **Loki** | prod (Docker) | `3100` | `23100` |
| **Prometheus** | prod (Docker) | `9090` | `29090` |

### 1.4. Inconsistencias de Nomenclatura (Naming)
*   **Carpetas de microservicios**: Mezcla de nombres cortos (`auth`, `catalogo`) con nombres que llevan sufijo (`orden-ms`, `pago-ms`).
*   **Service Name en Eureka y Artifacts**: Registros bajo nombres simples como `auth` o `catalogo`. El estándar de Pagatu define el uso del prefijo corporativo: `pagatu-auth-ms`, `pagatu-catalogo-ms`, etc.
*   **Paquetes Base Java**: Inconsistencia en nombres compuestos (`com.upeu.ordenms` vs `com.upeu.pagoms`).
*   **Idioma Técnico**: `orden-ms` y `pago-ms` utilizan paquetes y clases en español (`controlador`, `servicio`, `repositorio`, `entidad`, `OrdenControlador`, `PagoControlador`), mientras que el resto usa estándares en inglés (`controller`, `service`, `repository`, `entity`).

### 1.5. Configuraciones Útiles a Migrar
*   **Resilience4j CB**: Configuración centralizada de ventanas y tasas de fallos para llamadas a servicios externos en los archivos `.yml`.
*   **Flyway Diferenciado**: Estrategia de desactivación de Flyway en desarrollo local (`ddl-auto: update`) y activación obligatoria con validación en producción (`ddl-auto: validate`).

---

## 2. ESTRATEGIA DE MIGRACIÓN Y MODERNIZACIÓN

### 2.1. Qué Migrar
1.  **Código del API Gateway**: Reubicar bajo `infra/gateway/` conservando filtros de CORS y enrutamiento dinámico `lb://`.
2.  **Lógica del Servidor de Autenticación**: Reubicar bajo `auth-ms/` conservando el cifrado de datos y tokenización local de Spring Security.
3.  **Mecanismos de Correlación**: Copiar `CorrelationIdFilter` y `FeignTraceConfig` a todos los microservicios core.

### 2.2. Qué Reescribir (Refactorizar)
1.  **Estandarización al Inglés**: Traducir nombres de clases, métodos y paquetes en `orden-ms` y `pago-ms` (ej. cambiar paquete `controlador` a `controller` y clase `OrdenControlador` a `OrdenController`).
2.  **Consolidación de Catálogo y Producto**: Fusionar entidades, repositorios y lógica de categorías y productos en el microservicio unificado **`catalogo-ms`**.
3.  **Naming de Eureka y Maven**: Actualizar todos los archivos `pom.xml` y propiedades `spring.application.name` para adoptar la sintaxis `<proyecto>-<contexto>-ms` (ej: `pagatu-orden-ms`).

### 2.3. Qué Eliminar
1.  **Microservicio `producto` independiente** (su funcionalidad es absorbida por `catalogo-ms`).
2.  **Controlador `GatewayInstanciasController.java`** de depuración.
3.  **Rutas absolutas fijas** (ej: `file:///C:/ms1/...`) en la configuración nativa de `config-server`.
4.  **Excepciones de seguridad temporales** que saltan la validación del JWT en producción.

### 2.4. Qué Modernizar (Cloud-Ready)
1.  **Valores por Defecto Inteligentes (Single-Profile)**: Configurar propiedades con fallbacks dinámicos (ej: `server.port: ${PORT:8080}`). Si no se inyectan variables, usa `localhost` y puertos estándar por defecto para desarrollo ágil en IDE; si se corre en Docker/Kubernetes, se inyectan variables del entorno de red.
2.  **Health Checks Nativos de Kubernetes**: Mapear probes de Kubernetes directamente a los endpoints `/actuator/health/liveness` y `/actuator/health/readiness` expuestos por Spring Actuator.
3.  **Logs Orientados a Salida Estándar**: Desactivar logs a archivos físicos en entornos productivos, imprimiendo únicamente a `stdout` en formato JSON estructurado.
4.  **Estructura de Manifiestos Separados**:
    *   `/k8s-local`: Incluye el microservicio y el StatefulSet de base de datos MySQL local para desarrollo local rápido (Minikube).
    *   `/k8s`: Incluye únicamente el Deployment del microservicio sin base de datos local (apunta a base de datos administrada del Centro de Datos).
5.  **Estrategia de Puertos Dinámicos (Local vs Docker / Kubernetes)**:
    *   **En Local (Entorno IDE):** Para simular alta disponibilidad (múltiples instancias en la misma máquina física) sin conflictos de puerto, el puerto se configura dinámicamente usando el argumento de JVM `-Dserver.port=0` (asignación de puerto aleatorio libre por el SO) o sobrescribiéndolo en el arranque (ej. `mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8083"`).
    *   **En Docker / Kubernetes:** Cada contenedor cuenta con su propio namespace e IP privada aislada. Todos los contenedores de un mismo microservicio pueden escuchar internamente en el mismo puerto estático (ej. `8080`) sin colisión. El balanceo de carga se realiza a nivel del API Gateway resolviendo las IPs internas devueltas por Eureka o Kubernetes DNS.

---

## 3. CONVENCIÓN DE PUERTOS DEL ECOSISTEMA PAGATU

### 3.1. Puertos Fijos (Infraestructura y Soporte)

Los componentes de infraestructura que actúan como punto de entrada único o como base de autoconfiguración del sistema deben conservar puertos estáticos y predecibles. Son los únicos servicios cuyo puerto debe ser conocido de antemano por el resto del ecosistema:

| Componente | Puerto DEV (Host) | Puerto PROD (Host) | Puerto Interno (Contenedor) |
|---|---|---|---|
| **`pagatu-gateway`** | `8080` | `8090` (coexistencia con DEV) | `8080` |
| **`pagatu-config`** | `8888` | `8888` | `8888` |
| **`pagatu-eureka`** | `8761` | `8761` | `8761` |
| **Keycloak** (IdP) | `9090` | `9090` | `8080` |
| **Angular Frontend** | `4200` (ng serve) | `80` (Nginx) | `80` |
| **Grafana** | `3000` | `3000` | `3000` |
| **Prometheus** | `9091` | `9091` | `9090` |
| **Loki** | `3100` | `3100` | `3100` |
| **Kafka Broker** | `29092` (host) | `29092` (host) | `9092` |

> **Nota sobre Gateway DEV vs PROD en paralelo:** Si el estudiante necesita levantar simultáneamente la versión DEV (IDE en `8080`) y la versión PROD (Docker Compose), el `compose.yml` expone el Gateway en el puerto `8090` del host (`-p 8090:8080`). De esta forma ambas versiones coexisten sin conflicto de sockets en el mismo host.

### 3.2. Puertos Dinámicos (Microservicios de Negocio)

Los microservicios de negocio registrados en Eureka no requieren un puerto fijo en el host:

*   **En DEV (IDE):** Se configura `server.port=0` para que el sistema operativo asigne un puerto libre aleatorio. Eureka registra automáticamente la IP y el puerto asignado.
*   **En PROD (Docker / Kubernetes):** Todos los contenedores de un mismo microservicio usan internamente el mismo puerto estático (ej. `8080`) sin colisión gracias al aislamiento de red por namespace de contenedor. El Gateway balancea la carga entre las instancias resolviendo sus IPs internas mediante el Service Registry.

---

## 4. MIGRACIÓN DE MOTOR DE BASE DE DATOS: MySQL → PostgreSQL

### 4.1. Decisión Técnica

Se decide migrar de **MySQL 8** a **PostgreSQL 16** como motor de persistencia para todos los microservicios de negocio de la arquitectura Pagatu.

**Justificación:**
*   La imagen `postgres:16-alpine` ocupa aproximadamente **79 MB**, frente a los **631 MB** de `mysql:8.4`. Reducción del 87% en tamaño de imagen base. Crítico en entornos de laboratorio donde el estudiante puede correr 6 o más contenedores de base de datos simultáneamente.
*   PostgreSQL consume significativamente menos RAM por conexión activa en comparación con MySQL 8, reduciendo el riesgo de saturar la memoria del equipo del estudiante durante las prácticas.

### 4.2. Convención de Puertos de Bases de Datos

El puerto interno en todos los contenedores PostgreSQL es el estándar **`5432`**. Los puertos expuestos al host en DEV son únicos por microservicio para permitir conexiones simultáneas desde herramientas de administración (DBeaver, pgAdmin, DataGrip):

| Microservicio | Base de Datos | Puerto Interno | Puerto Host DEV | Puerto Host PROD |
|---|---|---|---|---|
| `pagatu-auth-ms` | `pagatu_auth_db` | `5432` | **`5401`** | ❌ No expuesto |
| `pagatu-ubigeo-ms` | `pagatu_ubigeo_db` | `5432` | **`5402`** | ❌ No expuesto |
| `pagatu-cliente-ms` | `pagatu_cliente_db` | `5432` | **`5403`** | ❌ No expuesto |
| `pagatu-catalogo-ms` | `pagatu_catalogo_db` | `5432` | **`5404`** | ❌ No expuesto |
| `pagatu-orden-ms` | `pagatu_orden_db` | `5432` | **`5405`** | ❌ No expuesto |
| `pagatu-pago-ms` | `pagatu_pago_db` | `5432` | **`5406`** | ❌ No expuesto |

> **Motivo de no exponer puertos de BD en PROD:** En el entorno contenerizado, el microservicio accede a su base de datos a través de la red interna de Docker usando el nombre del servicio como hostname (ej. `postgres-orden:5432`). Exponer el puerto al host en producción representa un riesgo de seguridad innecesario.

### 4.3. Cambios Técnicos Requeridos en los Proyectos Spring Boot

#### Dependencia Maven (`pom.xml`):
```xml
<!-- ELIMINAR -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
<!-- ELIMINAR si existe -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>

<!-- AGREGAR -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### URL de Conexión JDBC en `config-repo`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:pagatu_orden_db}
    username: ${DB_USER:pagatu}
    password: ${DB_PASS:pagatu}
    driver-class-name: org.postgresql.Driver
```

#### `compose-dev.yml` (con puerto expuesto al host para herramientas de administración):
```yaml
services:
  postgres-orden:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: pagatu_orden_db
      POSTGRES_USER: pagatu
      POSTGRES_PASSWORD: pagatu
    ports:
      - "5405:5432"
    volumes:
      - postgres_orden_dev_data:/var/lib/postgresql/data
```

#### `compose.yml` (sin puerto expuesto al host — solo acceso interno):
```yaml
services:
  postgres-orden:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: ${ORDEN_DB_NAME}
      POSTGRES_USER: ${ORDEN_DB_USER}
      POSTGRES_PASSWORD: ${ORDEN_DB_PASSWORD}
    volumes:
      - postgres_orden_data:/var/lib/postgresql/data
    networks:
      - orden-internal-net

  pagatu-orden-ms:
    build: .
    environment:
      DB_HOST: postgres-orden
      DB_PORT: 5432
    networks:
      - orden-internal-net
      - ms-net
```

> **Nota sobre Flyway:** No se requiere cambio en `flyway-core`. Flyway detecta automáticamente el dialecto de PostgreSQL a través del driver JDBC activo en el classpath.

