package com.elemental.licitapp.Licitaciones.application.ports.out;

import com.elemental.licitapp.Licitaciones.domain.entity.EstadoProceso;
import com.elemental.licitapp.Licitaciones.domain.entity.FiltroLicitaciones;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SecopApiPort {

    Page<Licitacion> obtenerLicitacionesObraPublica(Pageable pageable, FiltroLicitaciones filtro);

    /**
     * Traduce el identificador del proceso ({@code CO1.REQ.*}), que es el que guarda el Cuadro
     * de Obra, al identificador de portafolio ({@code CO1.BDOS.*}), que es la llave con la que
     * se consultan los documentos y las ofertas. Vacío si SECOP no lo publica.
     */
    Optional<String> resolverPortafolio(String idDelProceso);

    /**
     * Fase y desenlace del proceso: si ya se adjudicó, a quién, por cuánto y contra cuántos.
     * Vacío cuando SECOP no conoce el identificador. Tampoco se persiste.
     */
    Optional<EstadoProceso> resolverEstadoProceso(String idDelProceso);

    /**
     * Departamentos con procesos de obra pública, tal como los escribe SECOP. Alimenta el
     * desplegable de filtro: se devuelven verbatim para que el valor elegido vuelva a coincidir.
     */
    List<String> obtenerDepartamentos();
}
