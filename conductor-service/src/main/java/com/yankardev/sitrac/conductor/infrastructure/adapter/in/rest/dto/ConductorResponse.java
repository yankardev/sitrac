package com.yankardev.sitrac.conductor.infrastructure.adapter.in.rest.dto;

import java.time.LocalDate;

public record ConductorResponse(
        Long id,
        String dni,
        String nombres,
        String apellidos,
        String numeroLicencia,
        String categoriaLicencia,
        LocalDate fechaVencimientoLicencia,
        String telefono,
        boolean disponible,
        boolean activo
) {
}
