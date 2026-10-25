package com.yankardev.sitrac.programacion.infrastructure.adapter.out.rest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "flota-service")
public interface FlotaFeignClient {

    @GetMapping("/api/tractos/{id}")
    TractoResponse obtenerTracto(@PathVariable("id") Long id);

    @GetMapping("/api/semirremolques/{id}")
    SemirremolqueResponse obtenerSemirremolque(@PathVariable("id") Long id);

    @PutMapping("/api/tractos/{id}/estado")
    void cambiarEstadoTracto(@PathVariable("id") Long id, @RequestBody EstadoUnidadRequest request);

    @PutMapping("/api/semirremolques/{id}/estado")
    void cambiarEstadoSemirremolque(@PathVariable("id") Long id, @RequestBody EstadoUnidadRequest request);

    record EstadoUnidadRequest(String estado) {}

    record TractoResponse(
            Long id,
            String placa,
            String marca,
            String modelo,
            Integer anio,
            BigDecimal capacidadToneladas,
            String estado,
            boolean activo
    ) {}

    record SemirremolqueResponse(
            Long id,
            String placa,
            String tipo,
            BigDecimal capacidadToneladas,
            String estado,
            boolean activo
    ) {}
}
