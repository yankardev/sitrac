package com.yankardev.sitrac.pedido.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.pedido.domain.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record EstadoPedidoRequest(
        @NotNull EstadoPedido estado
) {}
