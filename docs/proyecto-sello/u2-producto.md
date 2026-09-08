# Distribuidas - Producto de Unidad 2

**Esta es la plantilla-ejemplo del producto de Unidad 2 de Desarrollo de Aplicaciones Distribuidas.** La estructura (comunicación resiliente, seguridad distribuida, mensajería asíncrona, consistencia distribuida, observabilidad, integración frontend) es exigible a todos. El contenido de `pagatu` es el ejemplo de referencia — cada equipo lo reemplaza por el de su propio dominio, declarado en su [Brief técnico](brief.md) de S2, sin cambiar la estructura.

!!! note "Contenido en construcción"
    Las sesiones S6-S11 (donde se construye cada capacidad de esta unidad) todavía no están publicadas. Esta plantilla ya fija la estructura exigible y la rúbrica, verificadas contra el sílabo; el detalle sesión por sesión del ejemplo `pagatu` (nombres de clase, endpoints, comandos exactos) se completa a medida que S6-S11 se publiquen — igual que hizo `u1-producto.md` con S1-S4.

## Producto

**Sistema distribuido seguro, resiliente, consistente, observable e integrado con cliente frontend.**

Implementa comunicación síncrona resiliente, seguridad distribuida, mensajería asíncrona, consistencia eventual en procesos de negocio, observabilidad operacional e integración frontend mediante el punto único de acceso — fortaleciendo el sistema base de Unidad 1 con los atributos de calidad que un sistema distribuido real necesita para operar.

## 1. Alcance de capacidades por sesión

**Tabla 1. De la sesión a la capacidad del sistema**

| Sesión | Capacidad que agrega | Ejemplo `pagatu` |
|---|---|---|
| S6 | Comunicación síncrona resiliente entre servicios (respuesta controlada ante fallos). | `pagatu-orden-ms` llama a `pagatu-catalogo-ms` con timeout, reintentos y *fallback* ante caída de instancia. |
| S7 | Seguridad distribuida y protección de rutas. | Identity Provider (Keycloak recomendado; JWT propio con Spring Security si el equipo justifica la alternativa) validado en cada microservicio, no solo en el Gateway. |
| S8 | Mensajería asíncrona entre servicios desacoplados. | Evento de negocio (ej. `orden-creada`) publicado y consumido vía Kafka, sin llamada síncrona directa entre los dos servicios. |
| S9 | Consistencia eventual, compensación e idempotencia en procesos de negocio. | El proceso de orden tolera fallos parciales sin dejar datos inconsistentes; reprocesar el mismo evento no duplica el efecto. |
| S10 | Observabilidad operacional: logs, health, métricas y paneles de diagnóstico. | Métricas y logs centralizados con trazabilidad entre servicios, panel de diagnóstico consultable. |
| S11 | Integración con cliente frontend mediante el Gateway. | Cliente web que consume la API únicamente a través de `pagatu-gateway`, con sesión y rutas protegidas según rol. |
| S12 (esta evaluación) | Ensambla todo lo anterior en un solo sistema y lo sustenta. | El sistema completo de Unidad 2 + sección 4 de la guía de evaluación. |

Lo que sustentas en S12 es **tu propio sistema**: las capacidades que tú construiste en S6-S11, sobre tu propio dominio — no el de `pagatu`.

## 2. Rúbrica de Evaluación

**Tabla 2. Rúbrica de evaluación de la Unidad 2**

| Criterio | Peso | CE / Nivel | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Calificación obtenida |
|---|---:|---|---|---|---|---|---:|
| 1. Comunicación entre servicios con respuesta controlada ante fallos | 12% | CE023-N3 | Timeout, reintentos y *fallback* verificados en vivo ante una caída provocada. | Mecanismo presente, verificado parcialmente. | Mecanismo definido, sin verificación clara ante fallo real. | No implementa respuesta controlada ante fallos. | |
| 2. Seguridad distribuida y protección de rutas | 16% | CE023-N3 | Autenticación y autorización verificadas en todos los microservicios propios, no solo en el Gateway. | Seguridad verificada en la mayoría de los servicios. | Seguridad presente solo en el Gateway o parcial. | No implementa seguridad distribuida. | |
| 3. Mensajería asíncrona entre servicios desacoplados | 16% | CE023-N3 | Evento de negocio publicado y consumido, con los dos servicios verificados de forma independiente (no acoplados). | Mensajería funcional, con acoplamiento parcial. | Mensajería definida, sin verificación clara del desacople. | No implementa mensajería asíncrona. | |
| 4. Consistencia eventual, compensación e idempotencia en procesos de negocio | 16% | CE023-N3 | Proceso de negocio probado con fallo parcial y reprocesamiento del mismo evento, sin inconsistencia ni duplicidad. | Consistencia verificada parcialmente (uno de los dos casos: compensación o idempotencia). | Consistencia mencionada, sin caso de prueba real. | No evidencia consistencia distribuida. | |
| 5. Logs, health, métricas y paneles de diagnóstico | 12% | CE023-N3 | Panel de diagnóstico operativo con métricas, logs y health checks correlacionados entre servicios. | Panel operativo, con correlación parcial entre servicios. | Métricas o logs presentes, sin panel ni correlación. | No presenta observabilidad verificable. | |
| 6. Cliente frontend integrado mediante Gateway | 8% | CE023-N3 | Cliente funcional que consume la API completa solo a través del Gateway, con sesión y rutas protegidas según rol. | Cliente funcional, con alguna llamada fuera del Gateway o protección parcial. | Cliente parcialmente integrado. | No presenta cliente frontend integrado. | |
| 7. Sustentación | 20% | CG | Sustenta con claridad y profesionalismo su aporte individual, respondiendo con precisión las preguntas del jurado. | Sustenta con solvencia, con detalles menores en claridad, orden o precisión. | Sustenta con dificultad; claridad, orden o precisión insuficientes. | No sustenta adecuadamente ni demuestra su aporte individual. | |

Nota final = suma de (`Peso` × `Puntos de la calificación obtenida`) / 100 × 20.

`CE023-N3` = Nivel 3 de la competencia CE023 (Programación). `CG` = Competencia General "Carácter y Aprendizaje Autónomo" del sílabo — no es CE023: los criterios 1-6 ya son la evidencia técnica, incluida su verificación en vivo; el criterio 7 verifica aporte individual y comunicación.

**Tabla 3. Subaspectos de la sustentación (Unidad 2)**

El criterio 7 se evalúa con los mismos 6 subaspectos de la sustentación integral del Proyecto Sello ([`u3-producto.md`](u3-producto.md#2-rubrica-de-evaluacion), Tabla 2) — exigibles desde esta sustentación de unidad, igual que en Unidad 1.

| Subaspecto | Qué observa en Unidad 2 |
|---|---|
| 1. Aporte individual | Cada integrante demuestra lo que construyó en Unidad 2 (Tabla 1 de esta guía). |
| 2. Comunicación y orden | Claridad, estructura, tiempo y lenguaje técnico durante la presentación. |
| 3. Presentación personal y actitud | Puntualidad, vestimenta limpia y adecuada, higiene, cabello ordenado, actitud profesional, respeto, honestidad y coherencia con los valores y principios cristianos de la institución. |
| 4. Repositorio y estándares | Topics académicos vigentes desde S2, organización, commits y reproducibilidad del sistema. |
| 5. MkDocs o equivalente | Documentación de Unidad 2 publicada, navegable y alineada con `u2-producto.md`. |
| 6. Pitch/demo ejecutiva | Introducción breve de cómo evolucionó el sistema desde Unidad 1 (no reemplaza la demo técnica de S12, la precede). |

Para usar la rúbrica con IA, solicita:

```text
Evalúa la sustentación y el producto (u2-producto.md, adaptado al dominio propio del equipo) usando la rúbrica de esta sección.
Para cada criterio selecciona la calificación obtenida: A=20, B=15, C=10, D=5.
Justifica brevemente cada nivel con evidencia concreta (fallo provocado, evento publicado/consumido, panel de diagnóstico, cliente en vivo).
Para el criterio 7, verifica explícitamente los 6 subaspectos de la Tabla 3 antes de asignar el nivel.
Calcula la nota final con la fórmula: suma de (Peso × Puntos de la calificación obtenida) / 100 × 20.
Indica 2 fortalezas y 2 recomendaciones para lo que sigue en Unidad III.
```

## 3. Trazabilidad y procedencia de la rúbrica

Los primeros seis criterios son cita literal de los criterios de evaluación del producto de la Unidad II en el sílabo de Desarrollo de Aplicaciones Distribuidas; el séptimo (Sustentación) corresponde a la sustentación exigida por el mismo sílabo (sesión 12, actividad 2).

**Con la malla curricular:** estos criterios profundizan la porción de plataforma distribuida del **Nivel 3 de CE023** (Programación) que Unidad 1 dejó como base — la integración completa de las cinco plataformas (consola, escritorio, web, distribuido, móvil) ocurre recién en `PI1`, Ciclo 8. El criterio 7 (Sustentación) es transversal y no forma parte de la definición de la competencia.
