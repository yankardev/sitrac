package com.yankardev.sitrac.conductor.application.service;

import com.yankardev.sitrac.conductor.application.exception.ConductorNoEncontradoException;
import com.yankardev.sitrac.conductor.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.conductor.domain.model.Conductor;
import com.yankardev.sitrac.conductor.domain.port.in.ConductorUseCase;
import com.yankardev.sitrac.conductor.domain.port.out.ConductorRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConductorService implements ConductorUseCase {

    private final ConductorRepositoryPort conductorRepositoryPort;

    @Override
    public Conductor crear(Conductor conductor) {
        if (conductorRepositoryPort.existePorDni(conductor.getDni())) {
            throw new ReglaNegocioException(
                    "Ya existe un conductor con el DNI " + conductor.getDni()
            );
        }

        if (conductorRepositoryPort.existePorNumeroLicencia(conductor.getNumeroLicencia())) {
            throw new ReglaNegocioException(
                    "Ya existe un conductor con la licencia " + conductor.getNumeroLicencia()
            );
        }

        Conductor nuevo = Conductor.builder()
                .dni(conductor.getDni())
                .nombres(conductor.getNombres())
                .apellidos(conductor.getApellidos())
                .numeroLicencia(conductor.getNumeroLicencia())
                .categoriaLicencia(conductor.getCategoriaLicencia())
                .fechaVencimientoLicencia(conductor.getFechaVencimientoLicencia())
                .telefono(conductor.getTelefono())
                .disponible(true)
                .activo(true)
                .build();

        return conductorRepositoryPort.guardar(nuevo);
    }

    @Override
    public List<Conductor> listar() {
        return conductorRepositoryPort.listar();
    }

    @Override
    public Conductor obtenerPorId(Long id) {
        return conductorRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ConductorNoEncontradoException(id));
    }

    @Override
    public Conductor actualizar(Long id, Conductor conductor) {
        Conductor actual = obtenerPorId(id);

        if (conductorRepositoryPort.existePorDniYIdDistinto(conductor.getDni(), id)) {
            throw new ReglaNegocioException(
                    "Ya existe otro conductor con el DNI " + conductor.getDni()
            );
        }

        if (conductorRepositoryPort.existePorNumeroLicenciaYIdDistinto(
                conductor.getNumeroLicencia(), id)) {
            throw new ReglaNegocioException(
                    "Ya existe otro conductor con la licencia " + conductor.getNumeroLicencia()
            );
        }

        Conductor actualizado = Conductor.builder()
                .id(actual.getId())
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

        return conductorRepositoryPort.guardar(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        conductorRepositoryPort.eliminarPorId(id);
    }
}
