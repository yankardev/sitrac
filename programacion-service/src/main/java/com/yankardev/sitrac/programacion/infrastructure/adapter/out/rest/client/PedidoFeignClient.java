package com.yankardev.sitrac.programacion.infrastructure.adapter.out.rest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;

@FeignClient(name = "pedido-service")
public interface PedidoFeignClient {

    @GetMapping("/api/pedidos/{id}")
    PedidoResponse obtener(@PathVariable("id") Long id);

    @PutMapping("/api/pedidos/{id}/estado")
    void cambiarEstado(@PathVariable("id") Long id, @RequestBody EstadoPedidoRequest request);

    record EstadoPedidoRequest(String estado) {}

    record PedidoResponse(
            Long id,
            Long clienteId,
            String tipoCarga,
            String descripcionCarga,
            BigDecimal toneladas,
            String origen,
            String destino,
            LocalDate fechaSolicitud,
            String estado
    ) {}
}
