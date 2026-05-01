# pagatu

`pagatu` es un proyecto de microservicios para pagos y comercio institucional. La arquitectura se construye por sesiones, empezando con un microservicio base independiente y evolucionando hacia configuracion centralizada, descubrimiento, gateway, Feign, resiliencia, Kafka, seguridad, Docker, Kubernetes y una aplicacion Angular.

## Documentos del Proyecto

| Documento | Rol |
|---|---|
| `README.md` | Entrada principal del repositorio. Resume el proposito del proyecto, el orden de trabajo por sesiones y donde leer cada tema. |
| `README_01_ACERCA_DEL_PROYECTO.md` | Describe la vision concreta de Pagatu: microservicios, releases, flujo principal, C4 nivel 1 y 2, despliegue por ambientes, infraestructura, stack tecnico y estructura real del repositorio. |
| `README_02_ARQUITECTURA_DETALLADA.md` | Contiene las vistas dinamicas, C4 nivel 3, C4 nivel 4 y ejemplos de componentes internos. |
| `OFICIAL_ESTRUCTURA_ESTANDAR.md` | Define el patron oficial y generico de estructura para proyectos de microservicios. No forma parte exclusiva de Pagatu. |


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

1. Leer `README_01_ACERCA_DEL_PROYECTO.md` para entender que se va a construir y revisar la arquitectura general.
2. Leer `README_02_ARQUITECTURA_DETALLADA.md` cuando se necesite bajar a vistas dinamicas, C4 nivel 3 o C4 nivel 4.
3. Leer `OFICIAL_ESTRUCTURA_ESTANDAR.md` para entender el patron oficial de carpetas.
4. Trabajar las sesiones en orden, empezando por un microservicio independiente antes de agregar infraestructura.
