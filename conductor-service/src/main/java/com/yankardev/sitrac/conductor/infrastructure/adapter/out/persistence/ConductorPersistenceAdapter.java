package com.yankardev.sitrac.conductor.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.conductor.domain.model.Conductor;
import com.yankardev.sitrac.conductor.domain.port.out.ConductorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConductorPersistenceAdapter implements ConductorRepositoryPort {

    private final ConductorJpaRepository repository;

    @Override
    public Conductor guardar(Conductor conductor) {
        return toDomain(repository.save(toEntity(conductor)));
    }

    @Override
    public List<Conductor> listar() {
        return repository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Conductor> buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existePorDni(String dni) {
        return repository.existsByDni(dni);
    }

    @Override
    public boolean existePorDniYIdDistinto(String dni, Long id) {
        return repository.existsByDniAndIdNot(dni, id);
    }

    @Override
    public boolean existePorNumeroLicencia(String numeroLicencia) {
        return repository.existsByNumeroLicencia(numeroLicencia);
    }

    @Override
    public boolean existePorNumeroLicenciaYIdDistinto(String numeroLicencia, Long id) {
        return repository.existsByNumeroLicenciaAndIdNot(numeroLicencia, id);
    }

    @Override
    public void eliminarPorId(Long id) {
        repository.deleteById(id);
    }

    private ConductorJpaEntity toEntity(Conductor conductor) {
        return ConductorJpaEntity.builder()
                .id(conductor.getId())
                .dni(conductor.getDni())
                .nombres(conductor.getNombres())
                .apellidos(conductor.getApellidos())
                .numeroLicencia(conductor.getNumeroLicencia())
                .categoriaLicencia(conductor.getCategoriaLicencia())
                .fechaVencimientoLicencia(conductor.getFechaVencimientoLicencia())
                .telefono(conductor.getTelefono())
                .disponible(conductor.isDisponible())
                .activo(conductor.isActivo())
                .build();
    }

    private Conductor toDomain(ConductorJpaEntity entity) {
        return Conductor.builder()
                .id(entity.getId())
                .dni(entity.getDni())
                .nombres(entity.getNombres())
                .apellidos(entity.getApellidos())
                .numeroLicencia(entity.getNumeroLicencia())
                .categoriaLicencia(entity.getCategoriaLicencia())
                .fechaVencimientoLicencia(entity.getFechaVencimientoLicencia())
                .telefono(entity.getTelefono())
                .disponible(entity.isDisponible())
                .activo(entity.isActivo())
                .build();
    }
}
