package com.elemental.licitapp.Empresa.infrastructure.out.repository;

import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpresaJpaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByNit(String nit);
}
