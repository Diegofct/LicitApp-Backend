---
name: Slice ConformacionConsorcio en AnalisisDeCumplimiento
description: Persistencia de la conformación del proponente (individual/consorcio/UT) para un cuadro de obra. Resultado del análisis de cumplimiento. Iteración 1 del flujo presentación→seguimiento.
type: project
---

Slice `ConformacionConsorcio` añadido al módulo `AnalisisDeCumplimiento` (2026-05-20).

**Why:** soportar el flujo "análisis → presentación en SECOP II → seguimiento". Sin este slice no había trazabilidad de con quién se presentó cada proceso. Es prerrequisito para la regla de bloqueo de transición a `PRESENTADO` (todo proceso presentado debe tener una conformación documentada).

**How to apply:** al añadir validaciones cruzadas con el cambio de estado a `PRESENTADO` en `CuadroDeObra`, este slice ya expone `ConsultarConsorcioUseCase.buscarPorCuadroDeObra(cuadroDeObraId)` para verificar existencia.

## Estructura

```
AnalisisDeCumplimiento/
├── application/
│   ├── ports/in/
│   │   ├── GuardarConformacionConsorcioUseCase    (nuevo)
│   │   └── ConsultarConsorcioUseCase              (nuevo)
│   ├── ports/out/
│   │   └── ConformacionConsorcioRepositoryPort    (nuevo)
│   └── service/
│       └── ConformacionConsorcioAppService        (@Service, implementa ambos use cases in)
├── domain/entity/
│   ├── ConformacionConsorcio                      (JPA + Lombok)
│   └── IntegranteConsorcio                        (JPA + Lombok, FK a ConformacionConsorcio)
└── infrastructure/
    ├── in/controller/
    │   ├── AnalisisCumplimientoController.java    (endpoints añadidos al controller existente)
    │   ├── dto/
    │   │   ├── GuardarConsorcioRequestDTO
    │   │   ├── IntegranteConsorcioRequestDTO
    │   │   ├── ConsorcioResponseDTO
    │   │   └── IntegranteConsorcioResponseDTO
    │   └── mapper/
    │       └── ConsorcioRequestMapper             (estático, sigue patrón de EmpresaRequestMapper)
    └── out/repository/
        ├── ConformacionConsorcioJpaRepository     (Spring Data, findByCuadroDeObraId)
        └── ConformacionConsorcioRepositoryAdapter (@Component, implementa puerto out)
```

## Endpoints REST

- `POST /analisis/consorcios` — guarda (o reemplaza si existe) la conformación para un cuadro.
- `GET /analisis/consorcios/cuadro/{cuadroDeObraId}` — consulta por cuadro; 404 si no existe.

## Modelo de datos (migración V9)

- `conformacion_consorcio`: id, cuadro_de_obra_id (UNIQUE + FK a cuadro_de_obra), tipo_participacion, fecha_conformacion, observaciones (TEXT).
- `integrante_consorcio`: id, conformacion_id (FK CASCADE), empresa_id (FK a empresas), porcentaje_participacion DECIMAL(5,2). UNIQUE compuesto (conformacion_id, empresa_id).

## Reglas de dominio validadas en `ConformacionConsorcioAppService.guardar`

1. `tipoParticipacion` no null; lista de integrantes no vacía.
2. `INDIVIDUAL` → exactamente 1 integrante con 100%.
3. `CONSORCIO` / `UNION_TEMPORAL` → al menos 2 integrantes.
4. Cada empresa única en la lista (no se repite).
5. Cada porcentaje en `(0, 100]`.
6. Suma de porcentajes = 100 con tolerancia 0.01.
7. Cada `empresaId` debe existir (via `ObtenerEmpresasPort.obtenerPorId`).
8. La existencia del `cuadroDeObraId` se valida por FK en BD (no se expandió el contrato del módulo CuadroDeObra por esto).

## Cambios colaterales

- `TipoParticipacion` ahora incluye `UNION_TEMPORAL` (antes solo INDIVIDUAL/CONSORCIO). No rompe nada: `AnalizadorCumplimientoService` solo usa los dos primeros explícitamente.
- El slice acepta UPSERT: si ya hay conformación para el cuadro, se reemplaza (mantiene mismo `id`).

## Iteraciones 2 y 3 completadas

- **Iteración 2** (ver `seguimiento_proceso.md`): nuevo módulo `SeguimientoProceso` con auto-inicialización al pasar a `PRESENTADO`.
- **Iteración 3**: bloqueo estricto. `CuadroDeObraService.aplicarCambioDeEstado` ahora exige que exista una `ConformacionConsorcio` antes de aceptar la transición a `PRESENTADO`. Implementado vía `CuadroDeObra.application.ports.out.ExisteConformacionConsorcioPort` y `ConformacionConsorcioModuloAdapter` (consume `ConsultarConsorcioUseCase` de este módulo).
- Consecuencia operativa: para presentar un proceso (incluso individualmente), Diego debe primero `POST /analisis/consorcios` con tipo `INDIVIDUAL` al 100% de la empresa elegida.
