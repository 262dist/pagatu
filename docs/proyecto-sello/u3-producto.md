# Distribuidas - Producto de Unidad 3 (Producto Final del curso)

**Esta es la plantilla-ejemplo del producto de Unidad 3 de Desarrollo de Aplicaciones Distribuidas.** Según el sílabo, Unidad 3 se llama "Validación y consolidación del producto del curso" — es decir, **el Producto Final del curso es, literalmente, el producto de Unidad 3**: no son dos entregas distintas, es el mismo sistema (Unidad 1 + Unidad 2) validado, estabilizado, documentado y defendido íntegramente. La estructura de esta plantilla es exigible a todos; el contenido de `pagatu` es el ejemplo de referencia — cada equipo lo reemplaza por el de su propio dominio, declarado en su [Brief técnico](brief.md) de S2.

## Producto

**Sistema distribuido de microservicios end-to-end, validado, documentado, estabilizado y defendido técnicamente.**

Integra los componentes desarrollados en las Unidades 1 y 2, valida flujos completos, estabiliza documentación y despliegue local, prepara evidencias técnicas y sustenta el producto final.

## 1. Componentes mínimos del producto final

Al cierre de Unidad 3, el sistema debe integrar — sin excepción — todo lo construido en las dos unidades anteriores:

- Microservicios con responsabilidades claras (Unidad 1).
- Configuración centralizada por ambiente (Unidad 1).
- Registro y descubrimiento de servicios (Unidad 1).
- API Gateway con rutas y balanceo (Unidad 1).
- Persistencia por servicio según el caso (Unidad 1).
- Comunicación síncrona resiliente (Unidad 2).
- Seguridad distribuida con autenticación, autorización y rutas protegidas (Unidad 2).
- Mensajería asíncrona con eventos de negocio (Unidad 2).
- Consistencia distribuida, compensación o idempotencia según el flujo (Unidad 2).
- Logs, health checks, métricas y paneles de observabilidad (Unidad 2).
- Frontend integrado mediante Gateway (Unidad 2).
- Docker o entorno reproducible.
- Documentación técnica y evidencias de ejecución.

## 2. Rúbrica de Evaluación

**Tabla 1. Rúbrica de evaluación de la Unidad 3 (Producto Final)**

| Criterio | Peso | CE / Nivel | A (20 pts) | B (15 pts) | C (10 pts) | D (5 pts) | Calificación obtenida |
|---|---:|---|---|---|---|---|---:|
| 1. Producto probado integralmente | 25% | CE023-N3 | Flujo end-to-end probado completo, con casos de éxito y de error, sobre el sistema real. | Flujo probado con cobertura parcial de casos de error. | Pruebas parciales, sin cubrir el flujo completo. | No presenta pruebas integrales del producto. | |
| 2. Documentación técnica completa y reproducible | 20% | CE023-N3 | Documentación publicada en MkDocs o equivalente, completa y reproducible por otra persona sin ayuda del equipo. | Documentación completa, con pasos de reproducción menores por aclarar. | Documentación parcial o con vacíos que dificultan reproducirla. | No presenta documentación técnica reproducible. | |
| 3. Evidencias de despliegue, seguridad, mensajería, consistencia y observabilidad | 25% | CE023-N3 | Evidencia verificable en vivo de las cinco capacidades, correlacionada entre sí. | Evidencia de la mayoría de las capacidades, con alguna solo documentada. | Evidencia parcial, limitada a una o dos capacidades. | No presenta evidencia verificable de estas capacidades. | |
| 4. Defensa grupal coherente, aporte individual verificable y demostración de las competencias desarrolladas | 30% | CE023-N3 (cierre) + CG | Explica y defiende el producto con solvencia; demuestra aporte individual, dominio técnico, comunicación clara, repositorio, documentación y actitud profesional. | Sustentación clara y funcional, con detalles menores en defensa técnica, evidencias, comunicación o documentación. | Sustentación parcial; dominio, evidencias, comunicación o aporte individual insuficientemente demostrados. | No sustenta adecuadamente, no demuestra autoría o no presenta evidencias mínimas del producto. | |

Nota final = suma de (`Peso` × `Puntos de la calificación obtenida`) / 100 × 20.

`CE023-N3` = Nivel 3 de la competencia CE023 (Programación). `CG` = Competencia General "Carácter y Aprendizaje Autónomo" del sílabo. El criterio 4 se etiqueta con ambas porque, a diferencia de Unidad 1 y 2 (donde la sustentación es un criterio agregado aparte), aquí el propio sílabo funde en un solo criterio la demostración técnica de las competencias desarrolladas (CE023, cierre) con la defensa grupal y el aporte individual (CG).

**Tabla 2. Subaspectos de la sustentación integral**

El criterio 4 se evalúa con los siguientes subaspectos. La sustentación integral representa como mínimo el 30% de la evaluación del producto final — coherente con el peso del criterio 4 en esta rúbrica.

| Subaspecto | Qué observa |
|---|---|
| 1. Defensa técnica | Explicación de arquitectura, comunicación entre servicios, decisiones técnicas, fallos controlados, limitaciones y evidencias generadas. |
| 2. Comunicación y orden | Claridad, estructura, tiempo y lenguaje técnico. |
| 3. Presentación personal y actitud | Puntualidad, vestimenta limpia y adecuada, higiene, cabello ordenado, actitud profesional, respeto, honestidad y coherencia con los valores y principios cristianos de la institución. |
| 4. Aporte individual | Cada integrante demuestra lo que hizo. |
| 5. Repositorio y estándares | Topics, organización, commits, documentación y reproducibilidad. |
| 6. MkDocs o equivalente | Documentación publicada, navegable y alineada al producto. |
| 7. Pitch/demo ejecutiva | Introducción clara del problema, solución y valor, seguida de una demo funcional. |

Para usar la rúbrica con IA, solicita:

```text
Evalúa la sustentación y el producto (u3-producto.md, adaptado al dominio propio del equipo) usando la rúbrica de esta sección.
Para cada criterio selecciona la calificación obtenida: A=20, B=15, C=10, D=5.
Justifica brevemente cada nivel con evidencia concreta del sistema completo (Unidades 1 y 2 integradas).
Para el criterio 4, verifica explícitamente los 7 subaspectos de la Tabla 2 antes de asignar el nivel.
Calcula la nota final con la fórmula: suma de (Peso × Puntos de la calificación obtenida) / 100 × 20.
Indica 2 fortalezas y 2 recomendaciones finales del producto.
```

## 3. Trazabilidad y procedencia de la rúbrica

Los cuatro criterios son cita literal de los criterios de evaluación del producto de la Unidad III en el sílabo de Desarrollo de Aplicaciones Distribuidas — a diferencia de Unidad 1 y Unidad 2, el sílabo de Unidad 3 ya incluye la defensa grupal y el aporte individual como parte de sus criterios oficiales (criterio 4), por eso no se agrega aquí un criterio de sustentación aparte.

**Con la malla curricular:** este producto final es el cierre del curso `DIST` dentro del **Nivel 3 de CE023** (Programación) — junto con `MOV` (plataforma móvil), completa la porción de plataformas que el Nivel 3 exige. La integración de las cinco plataformas (consola, escritorio, web, distribuido, móvil) en un solo sistema real ocurre recién en `PI1`, Ciclo 8 — este producto final de `DIST` es evidencia de una sola de esas cinco plataformas, no del cierre de toda la línea de Software.

## 4. Secuencia sugerida de presentación

La presentación puede organizarse con una secuencia breve de apoyo visual. El video pitch o introducción ejecutiva abre la sustentación y no reemplaza la demo ni la defensa técnica.

**Tabla 3. Secuencia sugerida de presentación**

| Orden | Slide o momento | Propósito | Competencia evidenciada |
|---:|---|---|---|
| 1 | Título del proyecto y equipo | Identificar el proyecto, integrantes y dominio elegido. | CE024 |
| 2 | Video pitch o introducción ejecutiva | Presentar problema, solución, valor y participación del equipo. | CE024 |
| 3 | Problema y alcance | Explicar el proceso distribuido y los límites del sistema. | CE023 |
| 4 | Arquitectura distribuida | Mostrar servicios, Gateway, configuración y comunicación. | CE023 |
| 5 | Seguridad | Evidenciar rutas protegidas, autenticación o autorización. | CE024 |
| 6 | Resiliencia y consistencia | Explicar fallos controlados, eventos, compensaciones o idempotencia. | CE022 + CE024 |
| 7 | Observabilidad | Mostrar logs, métricas, health checks o paneles. | CE024 |
| 8 | Integración frontend | Explicar cómo el cliente consume los servicios reales. | CE023 |
| 9 | Demo end-to-end | Ejecutar el flujo principal del sistema distribuido. | CE023 + CE024 |
| 10 | Aporte individual | Indicar qué hizo cada integrante. | CE024 |
| 11 | Repositorio y estándares | Mostrar repositorio, topics, estructura, documentación publicada en MkDocs o equivalente, y forma de ejecución. | CE024 |
| 12 | Limitaciones y mejoras | Reconocer límites del producto y mejoras posibles. | CE024 |

## 5. Documentación y reporte del producto final

### 5.1 Plantilla mínima de documentación MkDocs o equivalente

La documentación publicada no reemplaza al informe. Su función es permitir que otra persona comprenda, ejecute, revise y verifique el producto desde el repositorio.

**Tabla 4. Plantilla mínima de documentación**

| Página o sección | Contenido mínimo | Evidencia esperada |
|---|---|---|
| Inicio | Nombre del proyecto, problema, solución, curso, integrantes y enlace al repositorio. | Presentación clara del producto. |
| Instalación o ejecución | Requisitos, dependencias, configuración y comandos para ejecutar el proyecto. | Instrucciones reproducibles. |
| Uso del sistema | Flujo principal, endpoints y casos de uso. | Guía breve para probar el producto. |
| Arquitectura o estructura | Diagrama, componentes, carpetas principales y decisiones técnicas. | Vista técnica comprensible. |
| Módulos o funcionalidades | Descripción de las funciones principales del producto. | Relación entre funcionalidades y problema. |
| Datos | Modelo, base de datos o estructura de almacenamiento por servicio. | Evidencia de gestión de datos. |
| Pruebas y evidencias | Casos de prueba, capturas, resultados y validaciones. | Verificación del funcionamiento. |
| Equipo y aporte individual | Integrantes, responsabilidades, aportes y evidencias de participación. | Autoría verificable. |
| Repositorio y estándares | Topics académicos, estructura, commits y criterios de reproducibilidad. | Cumplimiento de estándares técnicos. |
| Limitaciones y mejoras | Restricciones del producto y mejoras futuras priorizadas. | Cierre reflexivo y realista. |

### 5.2 Plantilla sugerida de informe del proyecto

El informe debe documentar el producto de manera breve, verificable y alineada a las competencias evaluadas. No reemplaza la demo ni la sustentación; organiza las evidencias del proyecto.

**Tabla 5. Plantilla sugerida de informe**

| Sección | Contenido mínimo | Evidencia esperada |
|---|---|---|
| Portada | Nombre del proyecto, curso, sección, integrantes, docente y semestre. | Datos completos del equipo. |
| Resumen del proyecto | Problema, solución distribuida y valor del producto. | Síntesis de 8 a 12 líneas. |
| Competencia y alcance | Competencia/capacidad del proyecto y competencias relacionadas. | CE023, CE022 y CE024 vinculadas al producto. |
| Flujo de negocio | Proceso distribuido, actores, servicios y límites. | Descripción del flujo end-to-end. |
| Arquitectura distribuida | Microservicios, Gateway, configuración, descubrimiento y comunicación. | Diagrama de arquitectura y componentes. |
| Datos y consistencia | Persistencia, eventos, compensaciones o idempotencia. | Evidencias de datos, mensajes y resultados. |
| Observabilidad | Logs, métricas, health checks o paneles. | Capturas, comandos o paneles. |
| Validación y pruebas | Pruebas de flujo, seguridad, fallos y resultados. | Tabla de pruebas y evidencias. |
| Repositorio y documentación | Repositorio, topics, estructura, comandos y documentación publicada. | URL del repositorio y MkDocs o equivalente. |
| Aporte individual | Responsabilidad de cada integrante. | Tabla de tareas, commits o evidencias por integrante. |
| Limitaciones y mejoras | Límites actuales y mejoras posibles. | Lista priorizada y realista. |
