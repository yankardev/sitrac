package com.yankardev.sitrac.combustible.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.combustible.domain.model.TipoAbastecimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AbastecimientoResponse(
        Long id,
        Long programacionId,
        Long viajeId,
        Long conductorId,
        Long tractoId,
        TipoAbastecimiento tipoAbastecimiento,
        LocalDateTime fechaHora,
        BigDecimal cantidadGalones,
        BigDecimal kilometraje,
        BigDecimal precioUnitario,
        BigDecimal costoTotal,
        String tanqueOrigen,
        String proveedor,
        String numeroComprobante,
        String observacion
) {}