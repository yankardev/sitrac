package com.yankardev.sitrac.conductor.domain.port.out;

import com.yankardev.sitrac.conductor.domain.model.Conductor;

import java.util.List;
import java.util.Optional;

public interface ConductorRepositoryPort {

    Conductor guardar(Conductor conductor);

    List<Conductor> listar();

    Optional<Conductor> buscarPorId(Long id);

    boolean existePorDni(String dni);

    boolean existePorDniYIdDistinto(String dni, Long id);

    boolean existePorNumeroLicencia(String numeroLicencia);

    boolean existePorNumeroLicenciaYIdDistinto(String numeroLicencia, Long id);

    void eliminarPorId(Long id);
}
