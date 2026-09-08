# S15 - Evaluación de la Unidad III

## 1. Propósito de la evaluación

Esta sesión no enseña contenido nuevo: cierra la Unidad III de **Desarrollo de Aplicaciones Distribuidas** y, con ella, el curso completo. El sílabo (sesión 15) define dos actividades para esta evaluación:

1. Resolver la evaluación teórico-práctica de los temas de la Unidad III.
2. Presentar y sustentar el Sistema distribuido de microservicios end-to-end, validado, documentado y estabilizado.

**Esta sesión no repite lo evaluado en S5 ni en S12**: la Unidad I (base) y la Unidad II (robustez) ya quedaron certificadas por separado; S15 evalúa la integración, validación y estabilización final de ambas juntas — el sistema completo, no una capacidad nueva.

**S16 (Continuación de la evaluación de la Unidad III)** no es una evaluación distinta: es la misma evaluación de esta guía, para los equipos o integrantes cuya presentación, sustentación o evidencia quedó pendiente en S15.

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
| S13 | Validación end-to-end del producto del curso | Cobertura del flujo principal, casos de error incluidos, y qué diferencia una prueba end-to-end de probar cada servicio por separado. |
| S14 | Revisión técnica y estabilización del producto | Qué se estabilizó (configuración, documentación, evidencias) y por qué un producto "que funciona en la demo" no es lo mismo que un producto reproducible. |

Preguntas de referencia (el docente puede formular equivalentes):

1. ¿Qué caso de error de tu flujo principal probaste de extremo a extremo, y qué componente lo hubiera dejado pasar si solo probabas cada servicio por separado?
2. ¿Qué tuviste que estabilizar entre la Unidad II y esta evaluación para que el sistema fuera reproducible por otra persona?
3. Si otra persona clona tu repositorio hoy, ¿qué pasos exactos necesita seguir para levantar el sistema completo, y dónde están documentados?
4. ¿Qué limitación real del producto reconoces, y por qué no la resolviste dentro del alcance del curso?

## 4. Sustentación del producto final

**Tabla 2. Distribución de tiempo por integrante**

| Momento | Tiempo | Propósito |
|---|---:|---|
| Video pitch / introducción ejecutiva | 3 min | Presentar problema, solución, valor del producto y participación del equipo. |
| Exposición técnica | 10 min | Presentar arquitectura, servicios, flujo distribuido, seguridad, eventos y observabilidad ([Tabla 3 de `u3-producto.md`](../proyecto-sello/u3-producto.md#4-secuencia-sugerida-de-presentacion)). |
| Demostración en vivo | 5 min | Ejecutar el flujo end-to-end, evidenciando Gateway, servicios, eventos, seguridad y monitoreo. |
| Preguntas individuales | 5 min | Verificar dominio y aporte propio, con base en la Tabla 1 y el producto completo. |

**Tabla 3. Entregables obligatorios**

| Entregable | Evidencia mínima | Criterio de aceptación |
|---|---|---|
| Producto final | [`u3-producto.md`](../proyecto-sello/u3-producto.md), adaptado al dominio propio del equipo | Integra y estabiliza lo construido en Unidad 1 y Unidad 2 |
| Evidencia de validación end-to-end | Flujo principal probado completo, con al menos un caso de error | Verificable en vivo, no solo documentada |
| Repositorio y documentación | Topics académicos vigentes, documentación completa publicada en MkDocs o equivalente, informe del proyecto | Reproducible por otra persona desde el repositorio, sin ayuda del equipo |
| Sustentación grupal con aporte individual | Video pitch + exposición + demo + defensa por integrante, con los 7 subaspectos de [`u3-producto.md`, Tabla 2](../proyecto-sello/u3-producto.md#2-rubrica-de-evaluacion) | Aporte individual verificable dentro de la defensa grupal |

Criterios mínimos de aceptación:

- El sistema completo (Unidad 1 + Unidad 2) arranca de extremo a extremo desde el repositorio, sin pasos no documentados.
- El flujo principal de negocio se prueba en vivo con al menos un caso de éxito y uno de error.
- La documentación (MkDocs o equivalente) permite a otra persona reproducir el sistema sin ayuda del equipo.
- El repositorio mantiene los topics académicos y evidencia de commits a lo largo del curso.
- La sustentación cubre los 7 subaspectos de la sustentación integral (`u3-producto.md`, Tabla 2), con aporte individual verificable de cada integrante.
- Cada integrante responde individualmente al menos una pregunta de la Tabla 1.

## 5. Rúbrica de evaluación

La rúbrica (4 criterios, cita literal de los criterios de evaluación del producto de la Unidad III en el sílabo de Desarrollo de Aplicaciones Distribuidas — el propio criterio 4 ya incluye la defensa grupal y el aporte individual, por eso no se agrega un criterio de sustentación aparte) vive en [`u3-producto.md`](../proyecto-sello/u3-producto.md#2-rubrica-de-evaluacion), junto con la plantilla del producto, los subaspectos de sustentación y su trazabilidad con la malla curricular (CE023 Nivel 3). Úsala directamente desde ahí para calificar la sustentación de esta sesión — no se duplica aquí.
