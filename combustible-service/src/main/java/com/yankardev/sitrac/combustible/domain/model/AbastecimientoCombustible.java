package com.yankardev.sitrac.combustible.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbastecimientoCombustible {
    private Long id;
    private Long programacionId;
    private Long viajeId;
    private Long conductorId;
    private Long tractoId;
    private TipoAbastecimiento tipoAbastecimiento;
    private LocalDateTime fechaHora;
    private BigDecimal cantidadGalones;
    private BigDecimal kilometraje;
    private BigDecimal precioUnitario;
    private BigDecimal costoTotal;
    private String tanqueOrigen;
    private String proveedor;
    private String numeroComprobante;
    private String observacion;
}