---
name: Módulo Resultados — estructura y endpoints
description: Slice Resultados, creado el 2026-05-26. Solo lectura sobre CuadroDeObra. Expone resumen con totales por estado y tasa de éxito (porcentaje), e historial paginado de procesos cerrados.
type: project
---

Slice **Resultados** creado el 2026-05-26.

**Why:** Diego necesitaba un dashboard de desempeño licitatorio (totales por estado, tasa de éxito, historial). Decidió no agregar campos nuevos a `CuadroDeObra` ni persistir estado propio; el módulo agrega sobre los datos existentes (`CuadroDeObraEstado` + `observacion` como motivo de no-adjudicación). Ver [[decision_resultados_lectura_pura]].

**How to apply:**
- Si se piden nuevos KPIs basados en estado del proceso, extender este slice antes de crear uno nuevo.
- Para enriquecer con datos de otro módulo (p. ej. fecha de adjudicación desde `SeguimientoProceso`), añadir un puerto OUT adicional + adapter, **no** consultar repositorios JPA de otros módulos.
- Mantener todos los métodos como `@Transactional(readOnly = true)`.

## Estructura del slice

```
Resultados/
├── application/
│   ├── ports/
│   │   ├── in/
│   │   │   ├── ConsultarHistorialResultadosUseCase.java
│   │   │   └── ConsultarResumenResultadosUseCase.java
│   │   └── out/
│   │       └── ConsultarCuadrosPort.java
│   └── service/
│       └── ResultadosService.java                    @Service
├── domain/
│   └── entity/
│       ├── ItemHistorialResultado.java               (record)
│       └── ResumenResultados.java                    (record)
└── infrastructure/
    ├── in/controller/
    │   ├── ResultadosController.java                 @RestController
    │   ├── dto/
    │   │   ├── ItemHistorialResultadoResponseDTO.java
    │   │   └── ResumenResultadosResponseDTO.java
    │   └── mapper/
    │       └── ResultadosResponseMapper.java         (estático)
    └── out/repository/
        └── ConsultarCuadrosAdapter.java              @Component
```

## Endpoints REST

1. `GET /resultados/resumen`
   - Response: `ResumenResultadosResponseDTO` con `totalProcesos`, `porPresentar`, `presentados`, `adjudicados`, `noAdjudicados`, `cancelados`, `procesosCerrados`, `tasaExitoPorcentaje`.
   - Pasa por `ConsultarResumenResultadosUseCase`.
2. `GET /resultados/historial` (paginable estándar Spring: `?page=&size=&sort=`)
   - Filtra **internamente** por estados terminales evaluables: `ADJUDICADO`, `NO_ADJUDICADO`. `CANCELADO` se excluye porque no representa resultado de evaluación.
   - Cada item incluye `observacion` (motivo libre de no-adjudicación cuando aplica).
   - Pasa por `ConsultarHistorialResultadosUseCase`.

## Reglas y fórmulas

- **Tasa de éxito** = `(adjudicados × 100) / (adjudicados + noAdjudicados)`, `BigDecimal` escala 2, `RoundingMode.HALF_UP`. Si `procesosCerrados == 0`, retorna `0.00` (sin división por cero).
- **`procesosCerrados`** = `adjudicados + noAdjudicados`. **No incluye `CANCELADO`** ni los estados aún en curso.
- **`totalProcesos`** = suma de los 5 estados (incluye CANCELADO y los aún en curso).
- Conteo agrupado en **una sola query SQL** (`select estado, count(c) ... group by estado`) en `CuadroDeObraJpaRepository.contarAgrupadoPorEstado()`. Sin N+1.

## Cambios necesarios en CuadroDeObra para soportar el slice

1. Nuevo puerto in público: `CuadroDeObra/application/ports/in/ConsultarCuadrosUseCase.java` (espejo de `ConsultarRequisitosUseCase`).
   - `contarPorEstado() : Map<CuadroDeObraEstado, Long>`
   - `listarPorEstados(estados, pageable) : Page<CuadroDeObra>`
2. `CuadroDeObraService` ahora implementa también `ConsultarCuadrosUseCase` (4 puertos in totales).
3. `CuadroDeObraRepositoryPort` ganó `contarPorEstado()`, implementado por el adapter que llena un `EnumMap` (incluso para estados sin filas, devuelve 0).
4. `CuadroDeObraJpaRepository.contarAgrupadoPorEstado()` con `@Query` JPQL agrupada.

## Dependencias cross-módulo (vía puerto)

- `Resultados.ConsultarCuadrosAdapter` → `CuadroDeObra.application.ports.in.ConsultarCuadrosUseCase`.
- El adapter mapea `CuadroDeObra` (entidad JPA) → `ItemHistorialResultado` (record) en el límite del slice, manteniendo el dominio Resultados libre de tipos JPA.

## Convención: nombres únicos para adaptadores cross-módulo

`AnalisisDeCumplimiento` ya tiene `infrastructure/out/repository/CuadroDeObraModuloAdapter` (implementa `ObtenerRequisitosPort`). Si se reutiliza ese mismo nombre simple en otro slice, Spring deriva el mismo bean name (`cuadroDeObraModuloAdapter`) y arranca con `ConflictingBeanDefinitionException`.

**Regla:** los adaptadores cross-módulo deben tener **nombre simple distinto** entre slices. Convención adoptada: nombrar el adaptador por el puerto OUT que implementa (`ConsultarCuadrosAdapter` implementa `ConsultarCuadrosPort`). Si en el futuro se crean más adaptadores hacia `CuadroDeObra` desde otros slices, mantener el mismo patrón (`<NombreDelPuerto>Adapter`).
