---
name: AnalisisDeCumplimiento estructura hexagonal post-migración
description: Estado del módulo AnalisisDeCumplimiento tras la migración a hexagonal completa. Estructura de paquetes, puertos, adaptadores y contratos REST preservados.
type: project
---

Mapa del módulo tras la migración a hexagonal (migración completada el 2026-05-12).

**Why:** la migración alineó AnalisisDeCumplimiento con la convención de Empresa/CuadroDeObra. Los tres módulos comparten ahora la misma organización de paquetes.

**How to apply:** al añadir nuevos casos de uso al módulo, respetar exactamente esta estructura. Para extender el módulo hacia nuevos endpoints, añadir un puerto en `application/ports/in/` + AppService en `application/service/` + endpoint en el controller existente.

## Estructura de paquetes final

```
AnalisisDeCumplimiento/
├── application/
│   ├── ports/
│   │   ├── in/
│   │   │   ├── CalcularCapacidadProcesoUseCase.java
│   │   │   └── EvaluarCumplimientoUseCase.java
│   │   └── out/
│   │       ├── ObtenerEmpresasPort.java
│   │       └── ObtenerRequisitosPort.java
│   └── service/
│       ├── AnalizadorCumplimientoService.java        @Service
│       ├── CalcularCapacidadProcesoAppService.java   @Service
│       └── EvaluarCumplimientoAppService.java        @Service
├── domain/
│   ├── entity/
│   │   ├── DetalleRequisito.java                     (record)
│   │   ├── ResultadoEvaluacion.java                  (record)
│   │   ├── SugerenciaConsorcio.java                  (record)
│   │   └── TipoParticipacion.java                    (enum)
│   └── service/reglas/
│       ├── ReglaCapacidadResidual.java               (utilidades estáticas, sin Spring)
│       └── ReglaExperiencia.java                     (utilidades estáticas, sin Spring)
└── infrastructure/
    ├── in/controller/
    │   ├── AnalisisCumplimientoController.java       @RestController
    │   ├── dto/
    │   │   ├── CalcularResidualRequestDTO.java
    │   │   ├── DetalleRequisitoDTO.java
    │   │   ├── EvaluarCumplimientoRequestDTO.java
    │   │   ├── EvaluarCumplimientoResponseDTO.java
    │   │   └── SugerenciaConsorcioDTO.java
    │   └── mapper/
    │       └── AnalisisCumplimientoResponseMapper.java   (estático)
    └── out/repository/
        ├── CuadroDeObraModuloAdapter.java            @Component
        └── EmpresaModuloAdapter.java                 @Component
```

## Endpoints REST (contrato preservado, sin cambios)

1. `POST /analisis/evaluar`
   - Request: `{ empresaId, cuadroDeObraId }`
   - Response: `{ empresaId, cuadroDeObraId, tipoParticipacion, cumpleGlobal, detalles[], sugerencias[] }`
   - Pasa por `EvaluarCumplimientoUseCase`.
2. `POST /analisis/calcular-residual-proceso-contratacion`
   - Request: `{ presupuestoOficial, anticipo }`
   - Response: `BigDecimal` (cuerpo plano)
   - Pasa por `CalcularCapacidadProcesoUseCase.calcularCRPC`.
3. `POST /analisis/calcular-capital-trabajo-demandado`
   - Request: idem.
   - Response: `BigDecimal` (cuerpo plano)
   - Pasa por `CalcularCapacidadProcesoUseCase.calcularCTd`.

## Dependencias cross-módulo (todas vía puerto)

- `EmpresaModuloAdapter` → `Empresa.application.ports.in.ConsultarEmpresasUseCase`.
- `CuadroDeObraModuloAdapter` → `CuadroDeObra.application.ports.in.ConsultarRequisitosUseCase` (puerto creado durante la migración; `CuadroDeObraService` lo implementa junto con `CuadroDeObraUseCase`).

Ya no se consume directamente el `RequisitoLicitacionJpaRepository` del módulo CuadroDeObra (fuga arquitectónica corregida).

## Cambios estructurales aplicados en la migración

1. `domain/port/{in,out}/` → `application/ports/{in,out}/`.
2. `domain/model/` → `domain/entity/`.
3. `domain/service/AnalizadorCumplimientoService` → `application/service/AnalizadorCumplimientoService` (con `@Service`; el dominio queda libre de Spring).
4. `application/EvaluarCumplimientoAppService` → `application/service/EvaluarCumplimientoAppService` (inyección por constructor, `AnalizadorCumplimientoService` ya no se instancia con `new`).
5. `infrastructure/adapter/in/web/` → `infrastructure/in/controller/`.
6. `infrastructure/adapter/out/persistence/` → `infrastructure/out/repository/`.
7. Nuevo `CalcularCapacidadProcesoUseCase` + `CalcularCapacidadProcesoAppService`: el controller ya no llama directamente a `ReglaCapacidadResidual`; entra por puerto.
8. Nuevo mapper estático `AnalisisCumplimientoResponseMapper` que extrae el mapeo `ResultadoEvaluacion → EvaluarCumplimientoResponseDTO` del controller.
9. Nuevo `ConsultarRequisitosUseCase` en `CuadroDeObra/application/ports/in/`, espejo de `ConsultarEmpresasUseCase`.

## Reglas de negocio implícitas (siguen vigentes, no se han modificado)

1. Mipyme/Mujer suma 2 al límite de contratos en `ReglaExperiencia` (`limite + 2` cuando `esMipymeOMujer`).
2. Detección de Mipyme/Mujer por substring de `tamanoEmpresa` (busca "micro", "pequeña", "mediana", "mipyme", "mujer"). Frágil ante tildes/casing.
3. Consorcio fijo al 50/50: `participacion = 0.5` en `AnalizadorCumplimientoService.evaluarConsorcio`. No se modela el porcentaje real.
4. Capacidad residual del consorcio = suma simple de las K individuales (sin ponderación por participación).
5. `obtenerUltimaK` toma el último elemento de la lista `capacidadesResiduales` asumiendo orden cronológico. Sin filtro por fecha.
6. Si `kResidualProceso` viene `null` se omite la regla, no se considera incumplimiento.
7. Si `valorSMMLV` del cuadro viene `null` o `0`, no se evalúa experiencia.

Estas reglas son candidatas a refinar más adelante (no hacían parte del scope de la migración).

## Deuda técnica reconocida

- Las entidades JPA de Empresa (`Empresa`, `IndicadoresFinancieros`, `Experiencia`, `CapacidadResidualProponente`) y CuadroDeObra (`RequisitoLicitacion`, `CuadroDeObra`) viajan al dominio de AnalisisDeCumplimiento como tipos de entrada/salida. Esto es **deuda técnica del proyecto completo** (no exclusiva de este slice): todo el dominio del proyecto convive con anotaciones `@Entity` directamente. La migración no resuelve esto.
- Las clases `ReglaCapacidadResidual` y `ReglaExperiencia` siguen siendo estáticas. Defensible mientras la lógica sea pura y sin estado.
