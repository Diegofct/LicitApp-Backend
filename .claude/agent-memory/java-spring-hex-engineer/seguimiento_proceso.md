---
name: Módulo SeguimientoProceso
description: Nuevo módulo hexagonal para el ciclo de vida post-presentación. Inicialización automática al transicionar a PRESENTADO y registro manual de eventos. Iteración 2 del flujo presentación→seguimiento.
type: project
---

Módulo nuevo `SeguimientoProceso` (creado 2026-05-20). Sigue convención de Empresa/CuadroDeObra/AnalisisDeCumplimiento.

**Why:** soportar pasos 13–22 del flujo BPMN (presentación → seguimiento → adjudicación). Diego añade manualmente eventos como subsanaciones, informes y audiencias para hacer seguimiento al proceso publicado en SECOP II.

**How to apply:** al añadir features que dependan del ciclo de vida post-presentación (notificaciones, dashboards, integración con SECOP II para autopoblar eventos), conectar al módulo via `RegistrarEventoUseCase`. La inicialización es responsabilidad de `CuadroDeObra` al cambiar a `PRESENTADO`.

## Estructura

```
SeguimientoProceso/
├── application/
│   ├── ports/in/
│   │   ├── InicializarSeguimientoUseCase     (público, lo consume CuadroDeObra)
│   │   ├── RegistrarEventoUseCase
│   │   └── ConsultarSeguimientoUseCase
│   ├── ports/out/
│   │   └── SeguimientoRepositoryPort
│   └── service/
│       └── SeguimientoService                @Service, implementa los 3 use cases in
├── domain/
│   ├── entity/
│   │   ├── SeguimientoProceso                (JPA, @OrderBy fechaEvento ASC)
│   │   └── EventoSeguimiento                 (JPA, FK lazy a SeguimientoProceso)
│   └── enums/
│       └── TipoEventoSeguimiento             (enum cerrado + OTRO)
└── infrastructure/
    ├── in/controller/
    │   ├── SeguimientoController             @RestController /seguimientos
    │   ├── dto/
    │   │   ├── RegistrarEventoRequestDTO
    │   │   ├── EventoSeguimientoResponseDTO
    │   │   └── SeguimientoResponseDTO
    │   └── mapper/
    │       └── SeguimientoRequestMapper      (estático)
    └── out/repository/
        ├── SeguimientoProcesoJpaRepository   (Spring Data, findByCuadroDeObraId)
        └── SeguimientoRepositoryAdapter      @Component
```

## Endpoints

- `POST /seguimientos/cuadro/{cuadroDeObraId}/eventos` — registra evento. Falla si no existe seguimiento (proceso aún no presentado).
- `GET /seguimientos/cuadro/{cuadroDeObraId}` — devuelve seguimiento con eventos ordenados por `fechaEvento ASC`.

## Tipos de evento (enum cerrado)

`OFERTA_PRESENTADA`, `PLAZO_SUBSANACION_ABIERTO`, `SUBSANACION_PRESENTADA`, `INFORME_EVALUACION_PRELIMINAR`, `OBSERVACIONES_AL_INFORME`, `INFORME_EVALUACION_DEFINITIVO`, `AUDIENCIA_PROGRAMADA`, `AUDIENCIA_REALIZADA`, `RESOLUCION_ADJUDICACION`, `DECLARATORIA_DESIERTA`, `OTRO`.

Si `tipo=OTRO`, la `descripcion` es obligatoria (validado en `SeguimientoService.registrar`).

## Modelo de datos (migración V10)

- `seguimiento_proceso`: id, cuadro_de_obra_id (UNIQUE + FK), fecha_inicio.
- `evento_seguimiento`: id, seguimiento_id (FK CASCADE), tipo VARCHAR(64), fecha_evento, descripcion TEXT, fecha_registro. Índice en seguimiento_id.

Distinción importante:
- `fechaEvento`: cuándo ocurre/ocurrió el evento (la marca Diego — ej. audiencia futura).
- `fechaRegistro`: cuándo se añadió al sistema (audit automático, `LocalDateTime.now()` en el AppService).

## Integración con CuadroDeObra (auto-inicialización)

`CuadroDeObra.application.ports.out.InicializarSeguimientoPort` define el contrato.
`CuadroDeObra.infrastructure.out.repository.SeguimientoModuloAdapter` lo implementa consumiendo `InicializarSeguimientoUseCase`.

`CuadroDeObraService.aplicarCambioDeEstado` devuelve `boolean` indicando si la transición resultó en `PRESENTADO`. Si sí, el caller (`updateEstado` o `updateCuadro`) llama a `inicializarSeguimientoPort.inicializar(id)` **después** del `save`, todavía dentro de la `@Transactional`. Esto garantiza que si la inicialización falla, el cambio de estado se revierte.

`InicializarSeguimientoUseCase.inicializar` es **idempotente**: si ya existe seguimiento para el cuadro retorna el existente sin crear eventos duplicados. Esto cubre retries y, en iteración 3, permite re-llamar sin efectos secundarios.

## Iteración 3 completada (2026-05-20) — bloqueo estricto

- `CuadroDeObra.application.ports.out.ExisteConformacionConsorcioPort` (`boolean existePara(Long)`).
- `CuadroDeObra.infrastructure.out.repository.ConformacionConsorcioModuloAdapter` consume `ConsultarConsorcioUseCase` del módulo AnalisisDeCumplimiento.
- En `CuadroDeObraService.aplicarCambioDeEstado`, antes de mutar el estado: si destino = `PRESENTADO` y no existe conformación, lanza `IllegalArgumentException` con mensaje que indica al usuario qué endpoint usar.
- Orden de validaciones en `aplicarCambioDeEstado`: (1) transición permitida según enum, (2) si destino=PRESENTADO, conformación existente. Solo después se muta el estado y se dispara la inicialización del seguimiento (en el caller).
