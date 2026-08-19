package com.elemental.licitapp.Licitaciones.application.ports.out;

import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SecopApiPort {

    Page<Licitacion> obtenerLicitacionesObraPublica(Pageable pageable, String entidad);

    /**
     * Traduce el identificador del proceso ({@code CO1.REQ.*}), que es el que guarda el Cuadro
     * de Obra, al identificador de portafolio ({@code CO1.BDOS.*}), que es la llave con la que
     * se consultan los documentos y las ofertas. Vacío si SECOP no lo publica.
     */
    Optional<String> resolverPortafolio(String idDelProceso);

    /**
     * URL del proceso en SECOP II a partir de su identificador. Vacío si SECOP no la publica.
     * No se guarda en base de datos: se resuelve cuando hace falta.
     */
    Optional<String> resolverUrlProceso(String idDelProceso);
}
