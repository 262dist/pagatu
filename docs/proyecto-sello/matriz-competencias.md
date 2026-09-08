# Matriz de Competencias — Desarrollo de Aplicaciones Distribuidas

**Propósito:** consolidar en una sola vista la trazabilidad completa `Competencia (SO) → Nivel → Sesión → Criterio de rúbrica → Peso → Umbral de logro → Última medición`, hoy repartida entre `malla-curricular-2024.md` (competencia → curso → nivel) y cada `uN-producto.md` (criterio → competencia). Es el artefacto que un acreditador (ICACIT u otro) revisa para verificar que las rúbricas del curso miden competencias del perfil de egreso, no solo entregables completos.

Este documento **no reemplaza** las rúbricas de `u1-producto.md`, `u2-producto.md` y `u3-producto.md` — las resume y las conecta con el marco institucional.

## 1. Competencias evaluadas en este curso

DIST aporta evidencia a dos competencias de naturaleza distinta, que no deben mezclarse en una sola fila de matriz:

| Competencia | Fuente | Naturaleza |
|---|---|---|
| **CE023 — Programación**, Nivel 3 | Malla curricular 2024, línea Ingeniería de Software | Técnica, específica del curso. Evidencia: `DIST` (junto con `MOV`). |
| **Competencia General — Carácter y Aprendizaje Autónomo** | Sílabo DIST, sección III (Competencia del perfil de egreso), 10% de la nota final | Transversal, no técnica. No es exclusiva de este curso. |

El criterio de "Sustentación" en Unidad 1 y Unidad 2 **no evidencia CE023** — los criterios técnicos de cada unidad ya incluyen su verificación en vivo, así que la Sustentación evidencia únicamente la Competencia General (ver la columna `CE / Nivel` de cada `uN-producto.md`). El criterio de cierre de Unidad 3 es distinto: el propio sílabo lo funde con la demostración de competencias, por eso sí lleva ambas etiquetas.

## 2. Matriz consolidada

**Tabla 1. CE023 — Programación, Nivel 3 (evidencia técnica)**

| Unidad | Sesión | Criterio de rúbrica | Peso en la unidad | Umbral de logro | Última medición |
|---|---|---|---:|---|---|
| U1 | S5 | 1. Servicio REST funcional y persistente | 12% | ≥70% de estudiantes en nivel B (15 pts) o superior | Pendiente — primera cohorte 2026-2 |
| U1 | S5 | 2. Configuración externa por ambiente | 12% | ≥70% en B o superior | Pendiente |
| U1 | S5 | 3. Registro y descubrimiento de servicios operativo | 16% | ≥70% en B o superior | Pendiente |
| U1 | S5 | 4. Punto único de acceso mediante Gateway | 16% | ≥70% en B o superior | Pendiente |
| U1 | S5 | 5. Distribución de tráfico entre instancias | 16% | ≥70% en B o superior | Pendiente |
| U1 | S5 | 6. Evidencias de ejecución reproducible y documentación técnica básica | 8% | ≥70% en B o superior | Pendiente |
| U2 | S12 | 1. Comunicación entre servicios con respuesta controlada ante fallos | 12% | ≥70% en B o superior | Pendiente — S6-S11 aún no dictadas |
| U2 | S12 | 2. Seguridad distribuida y protección de rutas | 16% | ≥70% en B o superior | Pendiente |
| U2 | S12 | 3. Mensajería asíncrona entre servicios desacoplados | 16% | ≥70% en B o superior | Pendiente |
| U2 | S12 | 4. Consistencia eventual, compensación e idempotencia | 16% | ≥70% en B o superior | Pendiente |
| U2 | S12 | 5. Logs, health, métricas y paneles de diagnóstico | 12% | ≥70% en B o superior | Pendiente |
| U2 | S12 | 6. Cliente frontend integrado mediante Gateway | 8% | ≥70% en B o superior | Pendiente |
| U3 | S15 | 1. Producto probado integralmente | 25% | ≥70% en B o superior | Pendiente — S13-S14 aún no dictadas |
| U3 | S15 | 2. Documentación técnica completa y reproducible | 20% | ≥70% en B o superior | Pendiente |
| U3 | S15 | 3. Evidencias de despliegue, seguridad, mensajería, consistencia y observabilidad | 25% | ≥70% en B o superior | Pendiente |

**Tabla 2. Competencia General — Carácter y Aprendizaje Autónomo (evidencia transversal)**

| Unidad | Sesión | Criterio de rúbrica | Peso en la unidad | Umbral de logro | Última medición |
|---|---|---|---:|---|---|
| U1 | S5 | 7. Sustentación | 20% | ≥70% en B o superior | Pendiente |
| U2 | S12 | 7. Sustentación | 20% | ≥70% en B o superior | Pendiente |
| U3 | S15 | 4. Defensa grupal, aporte individual y demostración de competencias (también CE023-N3, cierre) | 30% | ≥70% en B o superior | Pendiente |

## 3. Umbral de logro

Se propone **≥70% de estudiantes con nivel B (15/20) o superior** por criterio, como umbral inicial uniforme — es el estándar más común en programas ICACIT/ABET para un primer ciclo de medición, antes de tener datos históricos propios que justifiquen ajustarlo por criterio. Este umbral se revisa después de la primera cohorte medida (ver sección 5).

## 4. Cómo se recolecta la medición (pendiente de implementar)

Hoy cada rúbrica tiene una columna "Calificación obtenida" que el docente llena **por estudiante, en su propia sustentación** — eso es evaluación individual, no medición agregada. Para que la columna "Última medición" de este documento deje de decir "Pendiente", falta:

1. Un registro (hoja de cálculo o base de datos) donde, al cerrar cada sustentación (S5, S12, S15), el docente vuelque la calificación obtenida de cada estudiante en cada criterio — no solo la nota final calculada.
2. Al cerrar la sección/cohorte, calcular el porcentaje de estudiantes en B o superior por criterio, y compararlo contra el umbral de la sección 3.
3. Registrar el resultado en la columna "Última medición" de este documento, con la fecha y el semestre (ej. "2026-2: 78% en B+ — cumple").

Sin este registro, las rúbricas siguen siendo válidas para calificar individualmente, pero no sirven todavía como evidencia de resultados agregados ante un acreditador.

## 5. Cierre del ciclo de mejora (pendiente de primera medición)

Cuando un criterio quede por debajo del umbral en una cohorte, este documento debe registrar: qué se ajustó (contenido de sesión, práctica adicional, claridad del criterio) y en qué semestre se aplicó el ajuste. Sin al menos una cohorte medida, esta sección queda como plantilla:

| Semestre medido | Criterio bajo umbral | Resultado | Acción tomada | Semestre de aplicación |
|---|---|---|---|---|
| — | — | — | — | — |
