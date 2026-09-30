package com.yankardev.sitrac.pedido.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.pedido.domain.model.EstadoPedido;
import com.yankardev.sitrac.pedido.domain.model.TipoCarga;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoRequest(
        @NotNull Long clienteId,
        @NotNull TipoCarga tipoCarga,
        @Size(max=200) String descripcionCarga,
        @NotNull @DecimalMin(value="0.01") BigDecimal toneladas,
        @NotBlank @Size(max=150) String origen,
        @NotBlank @Size(max=150) String destino,
        @NotNull LocalDate fechaSolicitud,
        EstadoPedido estado
) {}
