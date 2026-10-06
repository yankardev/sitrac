package com.yankardev.sitrac.conductor.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConductorJpaRepository extends JpaRepository<ConductorJpaEntity, Long> {

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);

    boolean existsByNumeroLicencia(String numeroLicencia);

    boolean existsByNumeroLicenciaAndIdNot(String numeroLicencia, Long id);
}
