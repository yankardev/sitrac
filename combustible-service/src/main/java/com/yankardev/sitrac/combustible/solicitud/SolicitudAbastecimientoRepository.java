package com.yankardev.sitrac.combustible.solicitud;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SolicitudAbastecimientoRepository extends JpaRepository<SolicitudAbastecimiento, Long> {
    Optional<SolicitudAbastecimiento> findByProgramacionId(Long programacionId);
}
