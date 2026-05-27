---
name: Resultados es módulo de solo lectura sobre CuadroDeObra
description: Decisión de Diego — el módulo Resultados agrega métricas e historial leyendo CuadroDeObra. No persiste estado propio. El motivo de no-adjudicación se toma del campo `observacion` ya existente, no se agrega un campo dedicado.
type: project
---

El módulo **Resultados** (nuevo slice, 2026-05-26) es de **solo lectura**: agrega métricas e historial leyendo el estado existente en `CuadroDeObra`.

**Why:** Diego rechazó explícitamente agregar un campo nuevo `motivoNoAdjudicacion` o crear una tabla propia en Resultados. El motivo de no-adjudicación se mantiene en el campo `observacion` (texto libre) que ya existe en `CuadroDeObra`. La fuente de verdad de "¿qué pasó con el proceso?" sigue siendo `CuadroDeObraEstado` + `observacion`.

**How to apply:**
- Resultados **no** tiene entidades JPA propias, **no** persiste nada.
- La comunicación con CuadroDeObra se hace a través de un puerto público de `CuadroDeObra/application/ports/in/` (estilo `ConsultarRequisitosUseCase`).
- Si en el futuro se requiere análisis estructurado del motivo (catálogo cerrado, KPIs por causa de rechazo, etc.), revisar primero con Diego antes de proponer ampliar `CuadroDeObra` o introducir una tabla propia. No asumir que esto cambia por iniciativa propia.
- Tasa de éxito tiene sentido como `adjudicados / (adjudicados + no_adjudicados)`, excluyendo POR_PRESENTAR/PRESENTADO/CANCELADO (que aún no son resultado evaluable).
- Ver [[analisis_cumplimiento_estado_actual]] para el patrón cross-módulo vía puerto in (`ConsultarRequisitosUseCase` + adapter en el slice consumidor).
