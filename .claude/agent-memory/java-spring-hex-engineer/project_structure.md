---
name: Convenciones de estructura del proyecto licitapp
description: Convenciones reales (paquetes, nombres, organización) verificadas en el código de licitapp. Útil para alinear nuevos slices/módulos.
type: project
---

Convenciones reales observadas en el código (no DDD, sin lenguaje ubicuo).

**Why:** el proyecto mezcla dos estilos de organización entre módulos. Saberlo evita proponer rutas inconsistentes.

**How to apply:** al crear archivos nuevos o reubicar durante la migración de AnalisisDeCumplimiento, alinearse al patrón del módulo donde se trabaja.

## Estructura por módulo

- **Empresa**: `Empresa/{application/{ports/{in,out},service},domain/entity,infrastructure/{in/controller/{dto,mapper},out/repository}}`. Adopta `application/ports/in` y `application/ports/out` separados. Mapper es `EmpresaRequestMapper` (clase utilitaria con métodos estáticos, no MapStruct). Tiene dos puertos de entrada: `EmpresaUseCase` (CRUD interno) y `ConsultarEmpresasUseCase` (puerto público de solo lectura para otros módulos).
- **CuadroDeObra**: `CuadroDeObra/{application/{ports/{in,out},service},domain/{entity,enums,exceptions},infrastructure/{in/controller/{dto,mapper},out/repository}}`. Mismo patrón general.
- **AnalisisDeCumplimiento**: `AnalisisDeCumplimiento/{application/{ports/{in,out},service},domain/{entity,service/reglas},infrastructure/{in/controller/{dto,mapper},out/repository}}`. Ya alineado con Empresa/CuadroDeObra tras la migración a hexagonal (2026-05-12). Tiene además `domain/service/reglas/` con utilidades estáticas puras (`ReglaCapacidadResidual`, `ReglaExperiencia`) que son específicas de este módulo. El AppService de evaluar (`EvaluarCumplimientoAppService`) implementa el puerto de entrada; existe además `AnalizadorCumplimientoService` en `application/service/` (no en domain) como bean Spring que orquesta la comparación. El mapper `AnalisisCumplimientoResponseMapper` (estático, sin Spring) sigue el patrón de `EmpresaRequestMapper`.

## Otros puntos
- Persistencia: las entidades de dominio están **anotadas con JPA** (`@Entity`, `@Table`, `@Column`) directamente. No hay separación entre modelo de dominio y modelo JPA por ahora — el dominio NO es puro en este sentido. Es deuda técnica conocida.
- Lombok activo: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.
- Inyección por constructor (manual o `@RequiredArgsConstructor`).
- DB: MySQL con `spring.jpa.hibernate.ddl-auto=update` + Flyway baseline en V1. Migraciones en `src/main/resources/db/migration/`.
- Excepciones: `GlobalExceptionHandler` global con `ResourceNotFoundException`, `IllegalArgumentException`, `MethodArgumentNotValidException`, etc.
- Idioma: nombres en español del negocio licitatorio (`Empresa`, `RequisitoLicitacion`, `CapacidadResidualProponente`, `IndicadoresFinancieros`).
- Naming de slices: en `AnalisisDeCumplimiento` el use case se llama `EvaluarCumplimientoAppService implements EvaluarCumplimientoUseCase`. Los puertos de salida tienen sufijo `Port` (`ObtenerEmpresasPort`, `ObtenerRequisitosPort`).
- Cross-module: el adaptador de salida del slice consume el puerto de entrada del otro módulo (no su repo JPA directamente). Ejs: `EmpresaModuloAdapter` usa `ConsultarEmpresasUseCase` del módulo Empresa; `CuadroDeObraModuloAdapter` usa `ConsultarRequisitosUseCase` del módulo CuadroDeObra. Los `ConsultarXxxUseCase` son puertos públicos de solo lectura, separados del puerto interno (`EmpresaUseCase`, `CuadroDeObraUseCase`); el `Service` del módulo implementa ambos.
