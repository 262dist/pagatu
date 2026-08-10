# Plan de trabajo DIST

Este documento es el rastreador vivo de avance de las guías de sesión de
DIST (`docs/sesiones/`). No confundir con el sílabo (contenido oficial, en
`silabo_dist_2026_2.md`) ni con `CLAUDE.md` (guía operativa de dónde vive
cada cosa en el repositorio).

## Estado actual

- **Repositorio recién iniciado (2026-08)**: `pagatu` reemplaza al repo
  anterior `261dist/ecom` (mismo dominio: comercio electrónico basado en
  microservicios) tras la reestructuración de carpetas y de org de GitHub
  (`261dist` -> `262dist`, `ecom` -> `pagatu`). El contenido técnico maduro
  del repo anterior se porta sesión por sesión hacia la plantilla vigente,
  no se copia tal cual.
- **S1**: única sesión existente hoy. Construida a partir de
  `C:\262\261\261dist\ecom\docs\sesiones\s01-arquitectura-base.md` (repo de
  referencia, solo lectura), migrada a la plantilla ampliada (Índice,
  Metodología, Motivación/1.6.1 Caso, Hoja de ruta en Aplica), con Java 17
  actualizado a Java 21 en todo el contenido, sin el paso de `git clone` a
  un repositorio base externo (`261dist/catalogo`, ya no aplica) — el CRUD
  completo (entidad, repositorio, DTO, mapper, excepciones, servicio,
  controlador, filtro de trazabilidad, migración Flyway) se escribe en
  línea dentro de la guía. Publicada en `mkdocs.yml`.
- **Este repo no tiene código propio todavía**: es solo documentación
  (MkDocs). El código de los microservicios (`catalogo-ms`, `producto-ms`,
  etc.) lo construye cada estudiante siguiendo las guías de sesión, bajo
  `services/<nombre-ms>/` en el repositorio de su propio proyecto — no
  existen archivos `.java`/`pom.xml`/Docker dentro de este repo de
  documentación.
- **Proyecto Sello**: la guía vive en `docs/proyecto-sello/index.md`,
  portada desde el repo de referencia y verificada contra
  `silabo_dist_2026_2.md` (nombres de unidad, alineamiento por sesiones,
  topics de repositorio).
- **`docs/propuesta-proyecto/`**: documentos de arquitectura propuesta para
  el proyecto del curso — fuera del alcance de este plan, no se toca desde
  aquí.

## Cómo se continúa

1. Leer el alcance oficial de la sesión en `silabo_dist_2026_2.md` (fila de
   esa sesión) y en `docs/index.md`.
2. Usar `docs/sesiones/S01_Construccion_Servicio_Base.md` como referencia
   estructural exacta: 1.1 Contexto, 1.2 Índice, 1.3 Propósito de
   aprendizaje, 1.4 Producto de sesión, 1.5 Metodología, 1.6
   Motivación/1.6.1 Caso, 1.7 Ubicación en el curso; Hoja de ruta antes de
   3.1; 4.1.1-4.1.5 en h4; sin agregar el cierre "Metodología para resolver
   problemas" (exclusivo de FP/POO).
3. Mantener los mismos valores de `Tiempo:` que ya usa S1 (20 min / 25 min /
   2h / 4h fuera del aula / 20 min) — no recalcular. La única diferencia de
   DIST frente a otros cursos del workspace (ADS, BD2, LP2, REQ, BD1, LP1)
   es que la actividad "Crea" dura 4h fuera del aula en vez de 2h; se
   mantiene así en toda la reconstrucción.
4. Si la sesión tiene contenido técnico maduro en el repo de referencia
   (`C:\262\261\261dist\ecom\docs\sesiones\`), portarlo fielmente: mismo
   código, mismos comandos, actualizando solo Java 17 -> Java 21, rutas del
   monorepo (`c:/262/2625dist/pagatu`, sin `ms1/ecom`) y typos/tildes
   faltantes. No asumir que existen repositorios base externos de
   `261dist` — cualquier paso de `git clone` a un repo base debe
   reemplazarse por instrucciones para escribir el código directamente.
5. Publicar la sesión en `mkdocs.yml` (ya tiene una entrada de nav por
   unidad; agregar la sesión dentro de la unidad correspondiente) y cerrar
   con un tag de git (`s02`, `s03`, ...) una vez verificada.

## Hoja de ruta

| Sesión | Foco | Estado |
|---|---|---|
| S1 | Construcción de un servicio base para un sistema distribuido | Hecho (plantilla alineada) |
| S2 | Gestión centralizada de configuración y ambientes | Pendiente (reconstruir) |
| S3 | Registro, descubrimiento y ejecución concurrente de servicios | Pendiente (reconstruir) |
| S4 | Punto único de acceso y distribución de tráfico | Pendiente (reconstruir) |
| S5 | Evaluación Unidad 1 | Pendiente (reconstruir) |
| S6 | Comunicación síncrona resiliente entre servicios | Pendiente (reconstruir) |
| S7 | Seguridad distribuida y control de acceso | Pendiente (reconstruir) |
| S8 | Mensajería asíncrona entre servicios | Pendiente (reconstruir) |
| S9 | Consistencia distribuida en procesos de negocio | Pendiente (reconstruir) |
| S10 | Observabilidad y diagnóstico de sistemas distribuidos | Pendiente (reconstruir) |
| S11 | Integración con cliente frontend | Pendiente (reconstruir) |
| S12 | Evaluación Unidad 2 | Pendiente (reconstruir) |
| S13 | Validación end-to-end del producto del curso | Pendiente (reconstruir) |
| S14 | Revisión técnica y estabilización del producto | Pendiente (reconstruir) |
| S15 | Defensa técnica (Evaluación Unidad 3) | Pendiente (reconstruir) |
| S16 | Evaluación final | Pendiente (reconstruir) |
