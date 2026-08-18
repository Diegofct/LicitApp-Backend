package com.elemental.licitapp.Sobre2.application.ports.in;

import com.elemental.licitapp.Sobre2.domain.entity.OferenteProceso;

import java.util.List;

public interface ImportarOferentesUseCase {

    /**
     * Trae de SECOP II los oferentes que presentaron Sobre 2 al proceso del cuadro y los
     * persiste. Es idempotente: reimportar actualiza los existentes en vez de duplicarlos.
     *
     * <p>Cuando el cuadro tiene {@code idDelProceso} la busqueda va por identificador y es
     * exacta. Si no lo tiene, o SECOP no resuelve el proceso, se degrada a buscar por
     * referencia y se avisa en {@code advertencias}.
     *
     * @param nitEntidad NIT de la entidad compradora. Opcional y normalmente innecesario: en
     *                   el camino por identificador no se usa (SECOP ya dice de que entidad
     *                   es el proceso, y si el NIT no coincide se advierte). Solo acota la
     *                   busqueda en el camino degradado por referencia
     * @param historico  true para consultar el dataset anterior a 2024 (mismo esquema)
     */
    ResultadoImportacion importar(Long cuadroId, String nitEntidad, boolean historico);

    /**
     * @param encontrados filas distintas de oferta halladas en SECOP (ya deduplicadas)
     * @param creados     oferentes nuevos
     * @param actualizados oferentes que ya existian y cambiaron
     * @param proponentesRegistrados cuantos radicaron oferta segun SECOP. Solo se consulta
     *                    cuando no llegaron valores, que es cuando aporta: distingue "nadie
     *                    se presento" de "hay N competidores pero sus precios siguen
     *                    sellados hasta la audiencia". Null si no aplica o SECOP no lo dice
     */
    record ResultadoImportacion(
            Long cuadroDeObraId,
            int encontrados,
            int creados,
            int actualizados,
            List<OferenteProceso> oferentes,
            List<String> advertencias,
            Integer proponentesRegistrados
    ) {
    }
}
