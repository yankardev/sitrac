package com.yankardev.sitrac.pedido.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.pedido.domain.model.EstadoPedido;
import com.yankardev.sitrac.pedido.domain.model.TipoCarga;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoResponse(
        Long id,
        Long clienteId,
        TipoCarga tipoCarga,
        String descripcionCarga,
        BigDecimal toneladas,
        String origen,
        String destino,
        LocalDate fechaSolicitud,
        EstadoPedido estado
) {}
