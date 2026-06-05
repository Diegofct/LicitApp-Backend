---
name: Motor de sugerencia de consorcios (AnalisisDeCumplimiento)
description: Cómo POST /analisis/evaluar sugiere consorcios cuando una empresa no cumple sola; decisiones de negocio validadas con Diego (experiencia sumada, reparto proporcional, top-K).
type: project
---

Rediseño del motor de sugerencia de consorcios en `AnalisisDeCumplimiento` (2026-06-04). Soporta el paso 6 del flujo interno ("Búsqueda de socio").

**Problema que resolvió:** la lógica anterior recombinaba todo al 50/50 fijo y exigía `cumpleGlobal`, lo que degradaba los requisitos que la empresa ya cumplía sola → casi nunca aparecía sugerencia. Diego reportó "no me sugiere nada".

## Decisiones de negocio validadas con Diego (no obvias)

1. **Experiencia en consorcio se SUMA, no se pondera por %.** Conforme a Documentos Tipo de obra de Colombia Compra Eficiente: cada integrante aporta el total de su experiencia SMMLV acreditada. Antes se multiplicaba por el % (bug). Corregido en `AnalizadorCumplimientoService.evaluarConsorcio`. K residual e índices financieros (liquidez, endeudamiento, RCI, ROE, ROA, capital de trabajo, patrimonio) SÍ van ponderados por %.
   - **Pendiente:** algunos pliegos exigen un % mínimo de participación del integrante que aporta experiencia. No modelado aún (no hay dato del pliego para ello).
2. **% de participación: configurable desde el front** vía `porcentajeSimulacion` (fracción 0.01–0.99, default 0.5) para la empresa solicitante. El complemento se reparte entre las candidatas.
3. **Reparto del complemento: proporcional al aporte** de cada candidata, no equitativo. El "aporte" es un peso adimensional = Σ(valorActual/valorRequerido) sobre los rubros deficitarios. Fallback equitativo si todos los pesos son 0. Encapsulado en `domain/service/RepartidorParticipacion` (puro).
4. **Varias alternativas (top-K):** se devuelven hasta K propuestas distintas, no una sola. K configurable en `application.properties` → `analisis.sugerencia.max-propuestas` (default 3).

## Algoritmo (BuscadorConsorcioService, @Service en application/service)

- Greedy por cobertura de déficit: parte del déficit individual de la solicitante y agrega la candidata que más requisitos faltantes cubre **sin degradar** los ya cumplidos.
- Genera variedad corriendo el greedy con cada candidata como socio de arranque; deduplica por conjunto de empresaIds; rankea por (viable primero, menos integrantes, más cobertura); top-K.
- Tope `MAX_INTEGRANTES = 4` (solicitante + 3 candidatas).
- Pool = todas las empresas menos la solicitante, con `getIndicadores() != null`.

## Contrato REST resultante

- `ResultadoEvaluacion.sugerencias` pasó de `List<SugerenciaConsorcio>` (empresa suelta) a `List<PropuestaConsorcio>` (consorcio completo con integrantes, %, requisitosCubiertos/Pendientes). Se eliminaron `SugerenciaConsorcio` y `SugerenciaConsorcioDTO`.
- DTOs nuevos: `PropuestaConsorcioDTO`, `IntegrantePropuestoDTO`.

## Deuda técnica conocida

- **N+1** al iterar `obtenerTodas()`: cada empresa carga sus 3 colecciones LAZY bajo demanda (funciona porque OSIV está activo — `spring.jpa.open-in-view` no está deshabilitado). No se hizo JOIN FETCH porque `Empresa` tiene 2 colecciones `List` y Hibernate lanzaría `MultipleBagFetchException`. Tolerable con pool pequeño (mono-tenant). Si se apaga OSIV o se mueve a hilo async, romperá con LazyInitializationException.
