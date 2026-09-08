# S5 - Evaluación de la Unidad I

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad I de **Desarrollo de Aplicaciones Distribuidas**. El sílabo (sesión 5) define dos actividades para esta evaluación:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad I (sesiones 1 a 4).
2. Presentar y sustentar el Sistema distribuido base funcional, configurable y preparado para múltiples instancias.

## 2. Producto evaluado

Del sílabo, el producto de la Unidad I es:

> Implementa la base técnica del sistema distribuido: un servicio REST funcional, configuración centralizada, descubrimiento dinámico, acceso por Gateway y ejecución concurrente de instancias.

El producto completo — plantilla-ejemplo con el contenido de `pagatu` — vive en [`u1-producto.md`](../proyecto-sello/u1-producto.md): alcance de servicios, contrato REST, configuración por ambiente y arquitectura del sistema distribuido base. La estructura es exigible a todos (servicio REST persistente, configuración externalizada por ambiente, registro y descubrimiento de servicios, punto único de acceso mediante Gateway, distribución de tráfico entre instancias); el contenido de `pagatu` se reemplaza por el del propio proyecto de cada equipo, declarado en su [Brief técnico](../proyecto-sello/brief.md) de S2.

### Lo que acumulaste sesión por sesión

Este producto no se construye en S5: se ensambla con lo que cada sesión anterior ya te pidió sobre tu propio proyecto.

**Tabla 1. De la sesión al sistema evaluado**

| Sesión | Qué produjiste (tu propio proyecto) | Dónde queda en `u1-producto.md` |
|---|---|---|
| S1 | Microservicio base con CRUD REST completo, persistencia en PostgreSQL con Flyway, Swagger, Actuator y ejecución con múltiples instancias en paralelo. | 1. Alcance de servicios y 2. Contrato REST |
| S2 | Configuración externalizada por ambiente (DEV/PROD) leída desde un Config Server propio. | 3. Configuración por ambiente |
| S3 | Registro y descubrimiento dinámico de tus servicios, con múltiples instancias verificadas de forma independiente. | 4. Arquitectura del sistema distribuido base |
| S4 | Punto único de acceso con Gateway, rutas resueltas por descubrimiento y balanceo de carga verificado entre instancias. | 4. Arquitectura del sistema distribuido base |
| S5 (esta sesión) | Ensamblas todo lo anterior en un sistema único y lo sustentas. | El sistema completo + sección 4 de esta guía |

Lo que sustentas en S5 es **tu propio sistema**: los servicios que tú construiste, sobre tu propio dominio — no el de `pagatu`. `u1-producto.md` muestra cómo se ve ese sistema terminado usando el ejemplo del docente; tu entregable real tiene la misma estructura, con el contenido que tú construiste en S1-S4.

## 3. Evaluación teórico-práctica (S1-S4)

Cubre los cuatro temas dictados antes de esta sesión. El docente puede tomarla escrita, oral o mixta.

**Tabla 2. Temario de la evaluación teórico-práctica**

| Sesión | Tema | Qué puede evaluar el docente |
|---|---|---|
| S1 | Arquitectura de un microservicio, persistencia y ejecución reproducible | Responsabilidad única, capas internas, PostgreSQL con Flyway, documentación con Swagger, verificación de salud y ejecución con múltiples instancias. |
| S2 | Gestión centralizada de configuración y ambientes | Config Server, externalización de configuración fuera del código, diferencias reales entre DEV y PROD. |
| S3 | Registro, descubrimiento y ejecución concurrente de servicios | Patrón Service Registry, registro dinámico, descubrimiento por nombre lógico, observabilidad de instancias registradas. |
| S4 | Punto único de acceso y distribución de tráfico | Gateway, rutas, resolución `lb://`, balanceo de carga round-robin y por qué es suficiente para instancias idénticas sin estado propio. |

Preguntas de referencia (el docente puede formular equivalentes):

1. ¿Por qué tu microservicio no debería depender de un puerto fijo asignado a mano, y cómo verificaste que corre con múltiples instancias en paralelo?
2. ¿Qué diferencia hay entre una propiedad fija en el código y una leída desde tu Config Server, y por qué esa diferencia importa entre DEV y PROD?
3. Si detienes una instancia de tu servicio, ¿cómo se entera tu registro de servicios de que ya no está disponible, y por qué no es instantáneo?
4. ¿Por qué la dirección `lb://` que usa tu Gateway no es una dirección real, y qué componente la resuelve?
5. ¿Qué algoritmo de balanceo de carga usa tu Gateway por defecto, y por qué es suficiente para instancias idénticas sin estado propio?

## 4. Sustentación del sistema

**Tabla 3. Distribución de tiempo por integrante**

| Momento | Tiempo | Propósito |
|---|---:|---|
| Presentación técnica | 8 min | Explicar el sistema (sección 2), las decisiones tomadas y su evolución desde S1. |
| Demo técnica | 5 min | Ejecutar el CRUD, el registro de instancias y el balanceo de carga en vivo, incluido un caso de error o caída de instancia. |
| Preguntas individuales | 5 min | Verificar dominio y aporte propio, con base en la Tabla 2. |

**Tabla 4. Entregables obligatorios**

| Entregable | Evidencia mínima | Criterio de aceptación |
|---|---|---|
| Producto de unidad | [`u1-producto.md`](../proyecto-sello/u1-producto.md), adaptado al dominio propio del equipo | Coherente con el sílabo y con el código real ejecutable |
| Evidencia de configuración | `-dev`/`-prod` verificables, sin credenciales versionadas | Config Server operativo, diferencias reales entre ambientes |
| Evidencia de registro y balanceo | Dashboard del registro con instancias `UP`, peticiones consecutivas resueltas por instancias distintas vía Gateway | Trazabilidad verificable con logs, no solo documentada |
| Repositorio y documentación | Topics académicos configurados ([Guía del proyecto](../proyecto-sello/index.md), sección 5), documentación de Unidad 1 publicada en MkDocs o equivalente | Reproducible por otra persona desde el repositorio |
| Sustentación individual | Video pitch breve (1-3 min) + defensa por integrante (sección 3), con los 7 subaspectos de la Tabla 5 de `u1-producto.md` | Autoría demostrada |

Secuencia sugerida de presentación (referencias a secciones de `u1-producto.md`):

1. Abrir con un video pitch breve (1-3 min) o introducción ejecutiva: qué construyó el equipo hasta ahora y por qué.
2. Presentar el alcance de servicios y el contrato REST.
3. Ejecutar el CRUD completo en vivo de un recurso: un caso de éxito y un caso inválido (`400`) o no encontrado (`404`).
4. Mostrar la configuración externalizada: el mismo artefacto, con valores distintos en DEV y en PROD.
5. Mostrar el dashboard del registro de servicios con las instancias registradas.
6. Ejecutar peticiones consecutivas a través del Gateway y evidenciar el balanceo entre instancias en los logs.
7. Detener una instancia en vivo y mostrar que el Gateway deja de enviarle tráfico sin que el cliente lo note.
8. Mostrar brevemente el repositorio (topics académicos) y la documentación de Unidad 1 publicada.
9. Cerrar explicando al menos una decisión propia distinta a la del ejemplo `pagatu` (dominio, recurso, o algún ajuste propio del patrón).

Criterios mínimos de aceptación:

- El sistema arranca en DEV con todos sus componentes: Config Server, registro de servicios, Gateway y al menos dos microservicios.
- Al menos un microservicio corre con dos instancias simultáneas, registradas y balanceadas.
- El CRUD completo del recurso principal funciona con un caso de éxito y uno de error.
- La configuración por ambiente (DEV/PROD) es verificable y no está hardcodeada en el código.
- El repositorio tiene los topics académicos configurados y la documentación de Unidad 1 publicada en MkDocs o equivalente.
- La sustentación cubre los 7 subaspectos de la Tabla 5 de `u1-producto.md` (defensa técnica, comunicación, presentación personal, aporte individual, repositorio, MkDocs, pitch/demo), no solo el dominio técnico.
- Cada integrante responde individualmente al menos una pregunta de la Tabla 2.

## 5. Rúbrica de evaluación

La rúbrica (7 criterios: 6 cita literal de los criterios de evaluación del producto de la Unidad I en el sílabo de Desarrollo de Aplicaciones Distribuidas + sustentación) vive en [`u1-producto.md`](../proyecto-sello/u1-producto.md#5-rubrica-de-evaluacion), junto con la plantilla del producto y su trazabilidad con la malla curricular (CE023 Nivel 3). Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
