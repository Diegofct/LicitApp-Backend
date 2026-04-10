package com.elemental.licitapp.Empresa.infrastructure.out.repository;

import com.elemental.licitapp.Empresa.application.ports.out.EmpresaRepositoryPort;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EmpresaRepositoryAdapter implements EmpresaRepositoryPort {

    private final EmpresaJpaRepository empresaJpaRepository;

    public EmpresaRepositoryAdapter(EmpresaJpaRepository empresaJpaRepository) {
        this.empresaJpaRepository = empresaJpaRepository;
    }

    @Override
    public Empresa guardar(Empresa empresa) {
        return empresaJpaRepository.save(empresa);
    }

    @Override
    public Optional<Empresa> buscarPorId(Long id) {
        return empresaJpaRepository.findById(id);
    }

    @Override
    public Optional<Empresa> buscarPorNit(String nit) {
        return empresaJpaRepository.findByNit(nit);
    }

    @Override
    public List<Empresa> listarTodas() {
        return empresaJpaRepository.findAll();
    }

    @Override
    public void eliminar(Long id) {
        empresaJpaRepository.deleteById(id);
    }

}
