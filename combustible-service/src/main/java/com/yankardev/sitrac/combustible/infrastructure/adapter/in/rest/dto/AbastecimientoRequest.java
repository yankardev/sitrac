package com.yankardev.sitrac.combustible.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.combustible.domain.model.TipoAbastecimiento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AbastecimientoRequest(
        @NotNull Long programacionId,
        Long viajeId,
        @NotNull Long conductorId,
        @NotNull Long tractoId,
        @NotNull TipoAbastecimiento tipoAbastecimiento,
        @NotNull LocalDateTime fechaHora,
        @NotNull @DecimalMin("0.01") BigDecimal cantidadGalones,
        @NotNull @DecimalMin("0.0") BigDecimal kilometraje,
        @DecimalMin("0.0") BigDecimal precioUnitario,
        @Size(max = 100) String tanqueOrigen,
        @Size(max = 150) String proveedor,
        @Size(max = 80) String numeroComprobante,
        @Size(max = 500) String observacion
) {}