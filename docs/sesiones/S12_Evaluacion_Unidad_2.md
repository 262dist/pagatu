# S12 - Evaluación de la Unidad II

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad II de **Desarrollo de Aplicaciones Distribuidas**. El sílabo (sesión 12) define dos actividades para esta evaluación:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad II (sesiones 6 a 11).
2. Presentar y sustentar el Sistema distribuido seguro, resiliente, consistente, observable e integrado con cliente frontend.

**Esta sesión no repite lo evaluado en S5**: la Unidad I (servicio base, configuración, registro, Gateway y balanceo) ya quedó certificada; S12 evalúa exclusivamente lo que se construyó encima en S6-S11.

## 2. Producto evaluado

Del sílabo, el producto de la Unidad II es:

> Fortalece el sistema distribuido incorporando atributos de calidad, integración frontend y evidencias técnicas de operación real.

El producto completo — plantilla-ejemplo con el contenido de `pagatu` — vive en [`u2-producto.md`](../proyecto-sello/u2-producto.md): comunicación resiliente, seguridad distribuida, mensajería asíncrona, consistencia distribuida, observabilidad e integración frontend. La estructura es exigible a todos; el contenido de `pagatu` se reemplaza por el del propio proyecto de cada equipo.

Lo que sustentas en S12 es **tu propio sistema**: las capacidades que tú construiste en S6-S11, sobre tu propio dominio — no el de `pagatu`. `u2-producto.md` (Tabla 1) muestra cómo cada sesión se ensambla en el sistema terminado usando el ejemplo del docente.

## 3. Evaluación teórico-práctica (S6-S11)

Cubre los seis temas dictados antes de esta sesión. El docente puede tomarla escrita, oral o mixta.

**Tabla 1. Temario de la evaluación teórico-práctica**

| Sesión | Tema | Qué puede evaluar el docente |
|---|---|---|
| S6 | Comunicación síncrona resiliente entre servicios | Timeout, reintentos, *fallback* y por qué un fallo en un servicio no debe propagarse en cascada al resto del sistema. |
| S7 | Seguridad distribuida y control de acceso | Autenticación, autorización, protección de rutas, y por qué cada microservicio debe validar el token, no solo el Gateway. |
| S8 | Mensajería asíncrona entre servicios | Publicación y consumo de eventos, desacople entre productor y consumidor, y diferencia con una llamada síncrona (REST/Feign). |
| S9 | Consistencia distribuida en procesos de negocio | Consistencia eventual, compensación e idempotencia — por qué un proceso distribuido no puede usar una transacción ACID única entre servicios. |
| S10 | Observabilidad y diagnóstico de sistemas distribuidos | Logs, health checks, métricas y para qué sirve correlacionar evidencia entre servicios distintos ante un fallo. |
| S11 | Integración con cliente frontend | Por qué el cliente consume la API solo a través del Gateway, y cómo se protegen las rutas según el rol del usuario. |

Preguntas de referencia (el docente puede formular equivalentes):

1. Si el servicio que llamas está caído, ¿qué evita que tu propio servicio también quede colgado esperando una respuesta que nunca llega?
2. ¿Por qué no basta con proteger las rutas en el Gateway, y qué pasa si un microservicio recibe una petición directa sin pasar por él?
3. ¿Qué gana tu sistema al comunicar dos servicios por eventos en vez de una llamada REST directa, y qué pierde (qué garantía ya no tienes)?
4. Si tu proceso de negocio falla a la mitad, ¿cómo evitas que quede en un estado inconsistente, y qué significa que sea idempotente?
5. ¿Qué evidencia concreta de tu panel de observabilidad te permitiría diagnosticar en qué servicio ocurrió una falla, sin revisar los logs uno por uno?
6. ¿Por qué tu cliente frontend nunca debería conocer la dirección de una instancia de microservicio directamente?

## 4. Sustentación del sistema

**Tabla 2. Distribución de tiempo por integrante**

| Momento | Tiempo | Propósito |
|---|---:|---|
| Video pitch / introducción ejecutiva | 2 min | Presentar en qué evolucionó el sistema desde la Unidad I. |
| Presentación técnica | 8 min | Explicar el sistema (sección 2), las decisiones tomadas y su evolución desde S5. |
| Demo técnica | 8 min | Ejecutar un fallo controlado, un evento de mensajería, el panel de observabilidad y el cliente frontend en vivo. |
| Preguntas individuales | 5 min | Verificar dominio y aporte propio, con base en la Tabla 1. |

**Tabla 3. Entregables obligatorios**

| Entregable | Evidencia mínima | Criterio de aceptación |
|---|---|---|
| Producto de unidad | [`u2-producto.md`](../proyecto-sello/u2-producto.md), adaptado al dominio propio del equipo | Coherente con el sílabo y con el código real ejecutable |
| Evidencia de resiliencia y seguridad | Fallo provocado con respuesta controlada; rutas protegidas verificadas en más de un microservicio | Trazabilidad verificable en vivo, no solo documentada |
| Evidencia de mensajería y consistencia | Evento publicado y consumido; caso de reprocesamiento sin duplicar el efecto | Verificable en logs o panel, no solo descrito |
| Evidencia de observabilidad y frontend | Panel de diagnóstico operativo; cliente consumiendo la API solo vía Gateway | Correlación real entre servicios, sesión protegida por rol |
| Repositorio y documentación | Topics académicos vigentes, documentación de Unidad 2 publicada en MkDocs o equivalente | Reproducible por otra persona desde el repositorio |
| Sustentación individual | Video pitch + defensa por integrante (sección 3), con los 7 subaspectos de la sustentación integral ([Guía del proyecto](../proyecto-sello/index.md), sección 6) | Autoría demostrada |

Secuencia sugerida de presentación (referencias a secciones de `u2-producto.md`):

1. Abrir con un video pitch breve: qué capacidades nuevas tiene el sistema desde la Unidad I.
2. Mostrar la comunicación resiliente: provocar la caída de una instancia y evidenciar el *fallback*.
3. Mostrar la seguridad distribuida: un acceso permitido y uno denegado, en más de un microservicio.
4. Publicar un evento de negocio y mostrar su consumo asíncrono en el servicio destino.
5. Provocar un fallo a mitad de un proceso de negocio y mostrar la compensación o el reprocesamiento idempotente.
6. Mostrar el panel de observabilidad correlacionando logs/métricas de al menos dos servicios.
7. Ejecutar un flujo completo desde el cliente frontend, mostrando una ruta protegida por rol.
8. Cerrar explicando al menos una decisión propia distinta a la del ejemplo `pagatu`.

Criterios mínimos de aceptación:

- Al menos un fallo de comunicación se provoca en vivo y el sistema responde de forma controlada, no con una caída en cascada.
- La seguridad se verifica en más de un microservicio, no solo en el Gateway.
- Al menos un evento de negocio se publica y se consume de forma verificable.
- Al menos un caso de consistencia (compensación o idempotencia) se prueba con un fallo real, no solo se describe.
- El panel de observabilidad correlaciona evidencia de al menos dos servicios distintos.
- El cliente frontend consume la API completa a través del Gateway, con al menos una ruta protegida por rol.
- La sustentación cubre los 7 subaspectos de la sustentación integral, no solo el dominio técnico.
- Cada integrante responde individualmente al menos una pregunta de la Tabla 1.

## 5. Rúbrica de evaluación

La rúbrica (7 criterios: 6 cita literal de los criterios de evaluación del producto de la Unidad II en el sílabo de Desarrollo de Aplicaciones Distribuidas + sustentación) vive en [`u2-producto.md`](../proyecto-sello/u2-producto.md#2-rubrica-de-evaluacion), junto con la plantilla del producto y su trazabilidad con la malla curricular (CE023 Nivel 3). Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
