package com.yankardev.sitrac.programacion.domain.port.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface OperacionConsultaPort {

    Optional<PedidoOperacion> buscarPedido(Long id);

    Optional<ConductorOperacion> buscarConductor(Long id);

    Optional<TractoOperacion> buscarTracto(Long id);

    Optional<SemirremolqueOperacion> buscarSemirremolque(Long id);

    void cambiarEstadoPedido(Long id, String estado);

    record PedidoOperacion(
            Long id,
            String tipoCarga,
            BigDecimal toneladas,
            String estado
    ) {}

    record ConductorOperacion(
            Long id,
            LocalDate fechaVencimientoLicencia,
            boolean disponible,
            boolean activo
    ) {}

    record TractoOperacion(
            Long id,
            BigDecimal capacidadToneladas,
            String estado,
            boolean activo
    ) {}

    record SemirremolqueOperacion(
            Long id,
            String tipo,
            BigDecimal capacidadToneladas,
            String estado,
            boolean activo
    ) {}
}
