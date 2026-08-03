# Sílabo del Curso: Arquitectura Evolutiva de Microservicios con Spring Boot, Kafka, Kubernetes y Angular

*   **Proyecto Integrador:** Pagatu (Sistema de comercio electrónico y pasarela de cobros institucional)
*   **Duración Total:** 48 horas académicas (12 Sesiones de 4 horas)
*   **Enfoque de Desarrollo:** Incremental paso a paso (Hands-on), orientado a principios Cloud-Ready (12-Factor Apps) y Observabilidad Transversal desde etapas tempranas.

---

## ESTRUCTURA CURRICULAR DETALLADA

### MÓDULO 1: Construcción de Microservicios Base (Horas: 8)

#### **Sesión 1: Microservicio Core y Base de Datos Contenerizada (4 horas)**
*   **Temas:**
    *   Diseño de arquitectura de microservicios limpia por capas: `controller`, `service`, `repository`, `entity`, `dto`.
    *   Aislamiento de persistencia local: creación de `compose-dev.yml` para levantar un contenedor **MySQL 8** dedicado.
    *   Control de versiones de base de datos con **Flyway** y persistencia relacional con **Spring Data JPA**.
    *   Mapeo de datos desacoplado con **MapStruct** y DTOs (`Request`/`Response`).
    *   Validaciones declarativas con `@Valid` y manejo global de excepciones con `@RestControllerAdvice` y `ProblemDetail`.
*   **Entregable Práctico:** Estructura base de `pagatu-cliente-ms` levantada localmente y conectada a su base de datos `pagatu_cliente_db` en Docker.

#### **Sesión 2: Centralización de Configuración con Spring Cloud Config (4 horas)**
*   **Temas:**
    *   Arquitectura de configuración centralizada: el patrón *Config Server*.
    *   Despliegue del servidor `pagatu-config` utilizando almacenamiento local nativo (`native` profile).
    *   Parámetros con valores por defecto inteligentes (fallbacks de entorno: `server.port: ${PORT:8080}`).
    *   Migración de propiedades de desarrollo de `pagatu-cliente-ms` al repositorio unificado `config-repo`.
*   **Entregable Práctico:** Microservicio base parametrizado que lee su configuración en tiempo de arranque desde el Config Server.

---

### MÓDULO 2: Enrutamiento y Observabilidad Transversal (Horas: 8)

#### **Sesión 3: Registro de Servicios y API Gateway (4 horas)**
*   **Temas:**
    *   Patrón *Service Registry* con **Netflix Eureka Server** (`pagatu-eureka`).
    *   Autoregistro, latidos (*heartbeats*) y descubrimiento de servicios del lado del cliente.
    *   Patrón *API Gateway* con **Spring Cloud Gateway** (`pagatu-gateway`) y enrutamiento dinámico `lb://`.
    *   Configuración de CORS para clientes web externos.
*   **Entregable Práctico:** Exposición de endpoints del cliente a través del Gateway (`/api/clientes/**`) resolviendo dinámicamente las instancias en Eureka.

#### **Sesión 4: Telemetría y Observabilidad Centralizada (4 horas)**
*   **Temas:**
    *   Monitoreo con **Spring Boot Actuator** y exposición de métricas Prometheus.
    *   Trazabilidad distribuida: inyección de Trace ID en el log de correlación (`MDC` con `traceId`).
    *   Despliegue del stack de plataforma de observabilidad contenerizada: **Loki** (logs), **Promtail** (recolector), **Prometheus** (métricas) y **Grafana** (dashboards).
*   **Entregable Práctico:** Despliegue de dashboards de Grafana para monitorear el Gateway y microservicios, rastreando flujos distribuidos mediante el Trace ID en tiempo real.

---

### MÓDULO 3: Integración Síncrona y Resiliencia (Horas: 8)

#### **Sesión 5: Comunicación Declarativa con OpenFeign (4 horas)**
*   **Temas:**
    *   Creación del microservicio geográfico `pagatu-ubigeo-ms` (con su base de datos contenerizada en `compose-dev.yml`).
    *   Comunicación síncrona mediante clientes declarativos **OpenFeign** en `pagatu-cliente-ms`.
    *   Propagación automática del encabezado `X-Trace-ID` en llamadas salientes con `RequestInterceptor`.
    *   *Monitoreo:* Visualización de las trazas de comunicación inter-servicio en los logs centralizados usando Grafana/Loki.
*   **Entregable Práctico:** Validación síncrona de códigos territoriales en el flujo de creación de clientes.

#### **Sesión 6: Resiliencia y Tolerancia a Fallos con Resilience4j (4 horas)**
*   **Temas:**
    *   Patrón **Circuit Breaker** (Disyuntor) con Resilience4j: transiciones de estados y umbrales de fallo.
    *   Mecanismos alternativos de recuperación con **Fallbacks**.
    *   Configuración de *Timeouts* y *Retries* en clientes Feign.
    *   *Monitoreo:* Visualización gráfica en Grafana del estado dinámico del disyuntor (*Closed*, *Open*, *Half-Open*) bajo pruebas de carga.
*   **Entregable Práctico:** Protección y contingencia contra caídas y latencias en la comunicación cliente-ubigeo.

---

### MÓDULO 4: Arquitectura Event-Driven (Horas: 8)

#### **Sesión 7: Mensajería Asíncrona con Apache Kafka (4 horas)**
*   **Temas:**
    *   Principios de arquitectura reactiva basada en eventos.
    *   Creación de `pagatu-orden-ms` y `pagatu-pago-ms`.
    *   Envío de eventos con `KafkaTemplate` y consumo concurrente usando `@KafkaListener`.
    *   Serialización/Deserialización de payloads JSON.
*   **Entregable Práctico:** Publicación del evento `orden.creada` y procesamiento asíncrono para generar transacciones financieras de cobro.

#### **Sesión 8: Patrones de Consumo Resiliente y Monitoreo de Kafka (4 horas)**
*   **Temas:**
    *   Estrategias de control de fallos en deserialización: `ErrorHandlingDeserializer`.
    *   Gestión de errores con `CommonErrorHandler` para evitar bucles por mensajes corruptos (*poison pills*).
    *   Monitoreo del broker Kafka en Grafana usando *Kafka Exporter* y administración visual mediante *Kafka UI*.
*   **Entregable Práctico:** Estabilización de consumidores contra fallos de datos y monitoreo de la tasa de consumo de eventos en vivo.

---

### MÓDULO 5: Seguridad Distribuida e Identidad (Horas: 8)

#### **Sesión 9: Autenticación Stateless Local con Servidor JWT (4 horas)**
*   **Temas:**
    *   Autenticación de microservicios sin estado utilizando JWT.
    *   Construcción de `pagatu-auth-ms` con MySQL y validación de credenciales.
    *   Configuración del Gateway como Resource Server que intercepta y valida la firma digital del JWT.
    *   Propagación de roles de usuario en las cabeceras HTTP internas.
*   **Entregable Práctico:** Aseguramiento de endpoints expuestos en el API Gateway mediante control de acceso por roles.

#### **Sesión 10: Evolución Corporativa: Integración con Keycloak (4 horas)**
*   **Temas:**
    *   Introducción a los servidores Identity and Access Management (IAM).
    *   Despliegue local de **Keycloak** como Identity Provider corporativo.
    *   Configuración de Realms, Clients (Frontend y Backend) y definición de roles.
    *   Migración del Gateway y microservicios para delegar la validación y autorización del token a Keycloak.
*   **Entregable Práctico:** Ecosistema securizado bajo estándares corporativos (OAuth2/OIDC) utilizando Keycloak.

---

### MÓDULO 6: Desarrollo Frontend y Cierre Cloud-Ready (Horas: 8)

#### **Sesión 11: Aplicación Frontend Angular y Nginx (4 horas)**
*   **Temas:**
    *   Consumo HTTP reactivo del Gateway desde Angular usando RxJS.
    *   Interceptor HTTP para agregar la cabecera `Authorization` (Bearer Token) y propagar el `X-Trace-ID` desde el navegador.
    *   Compilación de producción y dockerización del frontend montado sobre un servidor **Nginx** Cloud-Ready.
    *   *Monitoreo:* Seguimiento en Grafana de un log originado en Angular y procesado a través de todo el backend.
*   **Entregable Práctico:** Portal Angular contenerizado que consume e inicia transacciones en el backend de forma segura y correlacionada.

#### **Sesión 12: Despliegue DevOps y Orquestación en Kubernetes (4 horas)**
*   **Temas:**
    *   Diseño de manifiestos Cloud-Ready: separación de `/k8s-local` (incluye bases de datos locales) de `/k8s` (sin persistencia local, preparado para RDS/nube).
    *   Sensores de salud para Kubernetes: configuración de `livenessProbe` y `readinessProbe` de Spring Boot Actuator en los Pods.
    *   Orquestación unificada en clúster local (Minikube).
*   **Entregable Práctico:** Despliegue orquestado y operativo de la solución completa Pagatu (Frontend + Backend + Infraestructura + Kafka + DBs) en Kubernetes local.

---

## METODOLOGÍA Y ESTRATEGIA PEDAGÓGICA (CLOUD-READY HANDS-ON)

1.  **Enfoque de Perfil Único Parametrizado:** Para evitar doble trabajo en clase configurando perfiles de `dev` y `prod`, todas las propiedades se parametrizan con valores por defecto (ej: `url: jdbc:mysql://${DB_HOST:localhost}:3306/db` y `server.port: ${PORT:8080}`). En el IDE local, la aplicación arranca con puertos dinámicos aleatorios (`-Dserver.port=0`) o puertos configurados si se levantan múltiples instancias para pruebas de alta disponibilidad; al ejecutarse en contenedores Docker/Kubernetes, el orquestador inyecta las variables de red y los puertos mapeados sin generar colisiones gracias al aislamiento del contenedor.
2.  **Desarrollo Híbrido Activo:** El 80% del tiempo de la sesión se programa en caliente directamente con la app corriendo en el IDE sobre el host (apoyándose en auto-reloads). Los últimos 15-20 minutos de la sesión se dedican a compilar el contenedor y verificar que la imagen corra limpia en Docker, previniendo fallos acumulativos.
3.  **Uso de Plantillas Estructuradas:** El docente provee el `Dockerfile` optimizado y plantillas de manifiestos/compose. Los estudiantes escriben el código de negocio Java y configuran las variables de interconexión.