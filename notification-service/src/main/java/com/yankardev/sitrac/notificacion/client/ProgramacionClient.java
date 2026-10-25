package com.yankardev.sitrac.notificacion.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "programacion-service")
public interface ProgramacionClient {

    @GetMapping("/api/programaciones/{id}")
    ProgramacionResponse obtener(@PathVariable("id") Long id);

    record ProgramacionResponse(Long id, Long pedidoId) {}
}
