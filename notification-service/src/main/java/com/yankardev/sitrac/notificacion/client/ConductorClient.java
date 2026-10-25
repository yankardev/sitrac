package com.yankardev.sitrac.notificacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "conductor-service")
public interface ConductorClient {

    @GetMapping("/api/conductores/{id}")
    ConductorResponse obtener(@PathVariable("id") Long id);

    record ConductorResponse(
            Long id,
            String dni,
            String nombres,
            String apellidos,
            String numeroLicencia,
            String categoriaLicencia,
            String fechaVencimientoLicencia,
            String telefono,
            String email,
            boolean disponible,
            boolean activo
    ) {}
}
