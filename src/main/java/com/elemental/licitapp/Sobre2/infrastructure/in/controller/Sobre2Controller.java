package com.elemental.licitapp.Sobre2.infrastructure.in.controller;

import com.elemental.licitapp.Sobre2.application.ports.in.AnalizarSobre2UseCase;
import com.elemental.licitapp.Sobre2.application.ports.in.ConsultarCompetidoresUseCase;
import com.elemental.licitapp.Sobre2.application.ports.in.GestionarOferentesUseCase;
import com.elemental.licitapp.Sobre2.application.ports.in.ImportarOferentesUseCase;
import com.elemental.licitapp.Sobre2.domain.enums.RegimenPonderacion;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.AnalisisSobre2ResponseDTO;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.ImportacionOferentesResponseDTO;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.OferenteProcesoRequestDTO;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.OferenteProcesoResponseDTO;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.ResumenCompetidorResponseDTO;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.mapper.Sobre2RequestMapper;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.mapper.Sobre2ResponseMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Sobre 2 (oferta economica). Cubre el tramo entre el informe de evaluacion definitivo y
 * la audiencia de adjudicacion: traer a los competidores que abrieron Sobre 2 y calcular
 * sobre que valor conviene presentarse.
 *
 * <p>La autorizacion NO se declara aqui: esta centralizada en
 * {@code Seguridad/infrastructure/config/SecurityConfig} (ANALISTA y ADMIN para /sobre-2/**).
 */
@RestController
@RequestMapping("sobre-2")
public class Sobre2Controller {

    private final ImportarOferentesUseCase importarOferentes;
    private final GestionarOferentesUseCase gestionarOferentes;
    private final AnalizarSobre2UseCase analizarSobre2;
    private final ConsultarCompetidoresUseCase consultarCompetidores;

    public Sobre2Controller(ImportarOferentesUseCase importarOferentes,
                            GestionarOferentesUseCase gestionarOferentes,
                            AnalizarSobre2UseCase analizarSobre2,
                            ConsultarCompetidoresUseCase consultarCompetidores) {
        this.importarOferentes = importarOferentes;
        this.gestionarOferentes = gestionarOferentes;
        this.analizarSobre2 = analizarSobre2;
        this.consultarCompetidores = consultarCompetidores;
    }

    /**
     * Importa desde SECOP II los oferentes que presentaron Sobre 2. Es idempotente.
     *
     * @param nitEntidad NIT de la entidad compradora. Override manual, casi siempre
     *                   innecesario: solo se usa si el proceso no se pudo resolver por
     *                   identificador. Se mantiene para no romper el contrato y para poder
     *                   acotar a mano la busqueda de cuadros que no existen en SECOP
     * @param historico  true para procesos anteriores a 2024 (otro dataset, mismo esquema)
     */
    @PostMapping("/{cuadroId}/importar")
    public ResponseEntity<ImportacionOferentesResponseDTO> importar(
            @PathVariable Long cuadroId,
            @RequestParam(required = false) String nitEntidad,
            @RequestParam(defaultValue = "false") boolean historico) {
        var resultado = importarOferentes.importar(cuadroId, nitEntidad, historico);
        return ResponseEntity.ok(Sobre2ResponseMapper.toResponse(resultado));
    }

    @GetMapping("/{cuadroId}/oferentes")
    public ResponseEntity<List<OferenteProcesoResponseDTO>> listarOferentes(@PathVariable Long cuadroId) {
        return ResponseEntity.ok(Sobre2ResponseMapper.toResponse(gestionarOferentes.listar(cuadroId)));
    }

    /** Alta manual: el ~10% de procesos no publica sus ofertas en el dataset. */
    @PostMapping("/{cuadroId}/oferentes")
    public ResponseEntity<OferenteProcesoResponseDTO> crearOferente(
            @PathVariable Long cuadroId,
            @Valid @RequestBody OferenteProcesoRequestDTO request) {
        var creado = gestionarOferentes.crear(cuadroId, Sobre2RequestMapper.toEntity(request));
        return new ResponseEntity<>(Sobre2ResponseMapper.toResponse(creado), HttpStatus.CREATED);
    }

    @PutMapping("/oferentes/{oferenteId}")
    public ResponseEntity<OferenteProcesoResponseDTO> actualizarOferente(
            @PathVariable Long oferenteId,
            @Valid @RequestBody OferenteProcesoRequestDTO request) {
        var actualizado = gestionarOferentes.actualizar(oferenteId, Sobre2RequestMapper.toEntity(request));
        return ResponseEntity.ok(Sobre2ResponseMapper.toResponse(actualizado));
    }

    @DeleteMapping("/oferentes/{oferenteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarOferente(@PathVariable Long oferenteId) {
        gestionarOferentes.eliminar(oferenteId);
    }

    /**
     * Calcula los metodos de ponderacion. Devuelve los cuatro del regimen porque la entidad
     * los sortea con la TRM del dia habil siguiente a la apertura del Sobre 2: al ofertar no
     * se sabe cual aplicara.
     *
     * @param valorCandidato valor que se piensa ofertar; se incluye en la muestra para ver
     *                       el puntaje y la posicion reales
     */
    @GetMapping("/{cuadroId}/analisis")
    public ResponseEntity<AnalisisSobre2ResponseDTO> analizar(
            @PathVariable Long cuadroId,
            @RequestParam(required = false) BigDecimal valorCandidato,
            @RequestParam(required = false) RegimenPonderacion regimen,
            @RequestParam(required = false) BigDecimal puntajeMaximo) {
        var analisis = analizarSobre2.analizar(cuadroId, regimen, valorCandidato, puntajeMaximo);
        return ResponseEntity.ok(Sobre2ResponseMapper.toResponse(analisis));
    }

    /** Inteligencia de competidores acumulada entre procesos (agrupada por nombre). */
    @GetMapping("/competidores")
    public ResponseEntity<List<ResumenCompetidorResponseDTO>> competidores(
            @RequestParam(required = false) String nombre) {
        var resumen = consultarCompetidores.resumenPorCompetidor(nombre).stream()
                .map(Sobre2ResponseMapper::toResponse)
                .toList();
        return ResponseEntity.ok(resumen);
    }
}
