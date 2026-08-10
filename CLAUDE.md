# DIST - pagatu

Workspace de documentación del curso **Desarrollo de Aplicaciones
Distribuidas** (DIST), UPeU 2026-2. Este repo es solo de documentación
(MkDocs): no hay código de referencia ni backend/frontend dentro de este
repositorio — la "Propuesta de proyecto" (ver más abajo) describe la
arquitectura de microservicios prevista, pero no la implementa aquí.

## Dónde está cada cosa

- `docs/silabo_dist_2026_2.md` — sílabo oficial **vigente** (no editar salvo
  pedido explícito). `docs/silabo_dist_2026_1.md` es la versión anterior,
  solo como referencia histórica.
- `docs/index.md` — página de bienvenida curada (propósito del curso,
  producto del curso "Producto U3").
- `docs/propuesta-proyecto/` — documentos de arquitectura propuesta para el
  proyecto del curso (contenido, estructura estándar, propuesta de
  migración, y varios `README_0X_*.md` con detalle de arquitectura). Son
  documentos de diseño/propuesta, no código ejecutable.
- `mkdocs.yml` — nav con "Inicio", "Sílabos" (2026-1/2026-2) y "Propuesta de
  proyecto".

## Convenciones

- No editar los archivos `silabo_dist_*.md` salvo que se pida explícitamente.
- Si se agregan guías de sesión (`S0X_*.md`) en el futuro, seguir el mismo
  patrón de otros cursos del workspace (`docs/sesiones/`, publicadas en
  `mkdocs.yml` bajo una sección por unidad) — no existen todavía.
- Este repo no tiene skill de sesión (`.claude/skills/`) porque no hay
  código ni sesiones implementadas aún.
