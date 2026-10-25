package com.yankardev.sitrac.notificacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "comercial-service")
public interface ClienteClient {
    @GetMapping("/api/clientes/{id}")
    ClienteResponse obtener(@PathVariable("id") Long id);
    record ClienteResponse(Long id, String nombreRazonSocial, String email, boolean activo) {}
}
