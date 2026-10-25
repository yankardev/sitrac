package com.yankardev.sitrac.programacion.infrastructure.adapter.out.rest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

@FeignClient(name = "recursos-service")
public interface ConductorFeignClient {

    @GetMapping("/api/conductores/{id}")
    ConductorResponse obtener(@PathVariable("id") Long id);

    @PutMapping("/api/conductores/{id}/disponibilidad")
    void cambiarDisponibilidad(@PathVariable("id") Long id, @RequestBody DisponibilidadRequest request);

    record DisponibilidadRequest(Boolean disponible) {}

    record ConductorResponse(
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
    ) {}
}
