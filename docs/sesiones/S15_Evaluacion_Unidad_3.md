# S15 - Evaluación de la Unidad III

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad III de **Desarrollo de Aplicaciones Distribuidas** y, con ella, el curso completo. El sílabo (sesión 15) define dos actividades para esta evaluación:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad III.
2. Presentar y sustentar el Sistema distribuido de microservicios end-to-end, validado, documentado y estabilizado.

**Esta sesión no repite lo evaluado en S5 ni en S12**: la Unidad I (base) y la Unidad II (robustez) ya quedaron certificadas por separado; S15 evalúa la integración, validación y estabilización final de ambas juntas — el sistema completo, no una capacidad nueva.

**S16 (Continuación de la evaluación de la Unidad III)** no es una evaluación distinta: es la misma evaluación de esta guía, para los equipos o integrantes cuya presentación, sustentación o evidencia quedó pendiente en S15 — y, para quienes ya sustentaron, el momento de entregar la versión final del producto con las observaciones del jurado incorporadas ([S16](S16_Continuacion_Evaluacion.md)).

## 2. Producto evaluado

Del sílabo, el producto de la Unidad III es:

> Consolida el producto final del curso, valida flujos completos, estabiliza la arquitectura y sustenta técnicamente las decisiones implementadas.

El producto completo — plantilla-ejemplo con el contenido de `pagatu` — vive en [`u3-producto.md`](../proyecto-sello/u3-producto.md): componentes mínimos del producto final, rúbrica, subaspectos de sustentación, secuencia de presentación y plantillas de documentación e informe. La estructura es exigible a todos; el contenido de `pagatu` se reemplaza por el del propio proyecto de cada equipo.

**Nota de alcance:** según el sílabo, Unidad 3 se llama "Validación y consolidación del producto del curso" — el producto de Unidad 3 **es** el Producto Final del curso, no una entrega distinta ni adicional.

## 3. Evaluación teórico-práctica (S13-S14)

Cubre la validación end-to-end y la estabilización dictadas antes de esta sesión. El docente puede tomarla escrita, oral o mixta.

**Tabla 1. Temario de la evaluación teórico-práctica**

| Sesión | Tema | Qué puede evaluar el docente |
|---|---|---|
| S13 | Validación end-to-end del producto del curso | Por qué validar con dos usuarios encuentra lo que probar con uno no encuentra; autorización de ruta (Gateway) frente a autorización de objeto (el servicio dueño del dato) y por qué un objeto ajeno responde `404` y no `403`; endpoints de servicio a servicio alcanzables desde el punto de entrada público; y cómo un mismo `traceId` permite seguir una petición por varios servicios, con su límite en Kafka. |
| S14 | Revisión técnica y estabilización del producto | Por qué la prueba de reproducibilidad se hace sobre una copia limpia; qué tipos de afirmación de una documentación envejecen primero (estados, rutas, puertos, comandos); qué contiene un manual de ejecución además del README; para qué sirve el plan alternativo de un guion de demostración; y qué costo tiene cada decisión de arquitectura. |

Preguntas de referencia (el docente puede formular equivalentes):

1. Un cliente autenticado pide por su identificador una orden que pertenece a otro cliente: ¿qué componente debe impedirlo, por qué no alcanza con el Gateway, y qué respuesta debe recibir y por qué?
2. ¿Qué endpoint de servicio a servicio de tu sistema es alcanzable desde el punto de entrada público, y cómo lo negaste sin romper el flujo legítimo?
3. Una petición atraviesa tres servicios: ¿cómo demuestras con tus logs que es la misma, y en qué punto de tu sistema se rompe esa correlación?
4. Si otra persona clona tu repositorio hoy, ¿qué pasos exactos necesita para levantar el sistema completo, dónde están documentados, y cuándo fue la última vez que alguien los siguió al pie de la letra?
5. ¿Qué afirmación de tu documentación dejó de ser cierta, cómo lo detectaste, y cómo evitas que vuelva a pasar?
6. ¿Qué limitación real del producto reconoces, y por qué no la resolviste dentro del alcance del curso?

Criterios mínimos de aceptación:

- El sistema completo (Unidad 1 + Unidad 2) arranca de extremo a extremo desde el repositorio, sin pasos no documentados.
- El flujo principal de negocio se prueba en vivo con al menos un caso de éxito y uno de error.
- La documentación (MkDocs o equivalente) permite a otra persona reproducir el sistema sin ayuda del equipo.
- El repositorio mantiene los topics académicos y evidencia de commits a lo largo del curso.
- La sustentación cubre los 7 subaspectos de la sustentación integral (`u3-producto.md`, Tabla 2), con aporte individual verificable de cada integrante.
- Cada integrante responde individualmente al menos una pregunta de la Tabla 1.

## 5. Rúbrica de evaluación

La rúbrica (4 criterios, cita literal de los criterios de evaluación del producto de la Unidad III en el sílabo de Desarrollo de Aplicaciones Distribuidas — el propio criterio 4 ya incluye la defensa grupal y el aporte individual, por eso no se agrega un criterio de sustentación aparte) vive en [`u3-producto.md`](../proyecto-sello/u3-producto.md#2-rubrica-de-evaluacion), junto con la plantilla del producto, los subaspectos de sustentación y su trazabilidad con la malla curricular (CE023 Nivel 3). Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
