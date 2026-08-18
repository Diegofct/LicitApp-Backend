package com.elemental.licitapp.Sobre2.domain.entity;

/**
 * En que momento del proceso esta el cuadro, visto desde la optica del Sobre 2. Sirve para
 * advertir (nunca para bloquear): el modulo tambien se usa sobre procesos historicos o
 * ajenos para acumular inteligencia de competidores.
 *
 * <p>Las dos senales son independientes y significan cosas distintas:</p>
 * <ul>
 *   <li>{@code listoParaDecidir}: hay informe de evaluacion definitivo, o sea que ya se sabe
 *       quien quedo habil y es el momento de decidir con que valor presentarse.</li>
 *   <li>{@code valoresDefinitivos}: el Sobre 2 ya se abrio en audiencia, asi que los valores
 *       de los competidores son publicos y fiables. Antes de eso SECOP suele publicar el
 *       presupuesto oficial como marcador de posicion.</li>
 * </ul>
 *
 * <p>El estado se expone como {@code String} a proposito: el enum es de CuadroDeObra y este
 * slice no debe acoplar su dominio al de otro modulo. La traduccion vive en el adaptador.</p>
 */
public record MomentoSobre2(
        String estadoCuadro,
        boolean listoParaDecidir,
        boolean valoresDefinitivos
) {
}
