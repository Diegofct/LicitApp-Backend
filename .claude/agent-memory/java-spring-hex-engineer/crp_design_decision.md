---
name: Decisión de diseño del CRP en licitapp
description: Dónde vive cada parte del cálculo de Capacidad Residual del Proponente (CRP) y por qué. Útil al tocar AnalisisDeCumplimiento o Empresa.
type: project
---

El CRP del proponente (CO, E, CT_ec, CF, SCE → resultadoK) está modelado como entidad `CapacidadResidualProponente` en el módulo **Empresa** (no en AnalisisDeCumplimiento), con relación `@OneToMany` desde `Empresa` (una empresa puede tener múltiples cálculos históricos de CRP).

**Why:** el CRP es información del proponente, no del análisis. AnalisisDeCumplimiento sólo lo **consume** para comparar contra el K exigido por el pliego. Esta decisión separa lógica de cálculo (Empresa) de lógica de comparación (AnalisisDeCumplimiento).

**How to apply:**
- El cálculo `calcularCRP(co, e, ct, cf, sce)` y `recalcular()` viven en `Empresa.domain.entity.CapacidadResidualProponente` como métodos `static`/de instancia.
- El módulo **AnalisisDeCumplimiento** lee la **última** capacidad residual de la empresa vía `obtenerUltimaK(empresa)` en `AnalizadorCumplimientoService`. No la recalcula.
- En `ReglaCapacidadResidual` (de AnalisisDeCumplimiento) **sólo viven los cálculos del PROCESO** (`calcularCRPC`, `calcularCTd`), no del proponente.
- Endpoints expuestos:
  - `POST /empresas/calcular-capacidad-residual` → calcula sin persistir (en vivo para el formulario Angular).
  - `POST /empresas/{id}/capacidad-residual` → guarda y recalcula.
  - `POST /empresas` (crear) y `PUT /empresas/{id}` (actualizar) → si el payload incluye `capacidadesResiduales`, las persiste y recalcula.
  - `POST /analisis/calcular-residual-proceso-contratacion` → CRPC (presupuesto - anticipo).
  - `POST /analisis/calcular-capital-trabajo-demandado` → CTd.
- El cálculo del CRP usa `BigDecimal` con escala 2 (moneda) y escala 4 (cálculos intermedios), `RoundingMode.HALF_UP`.
- El campo persistido en BD es `resultado_k` (tabla `capacidad_residual_proponente`).
