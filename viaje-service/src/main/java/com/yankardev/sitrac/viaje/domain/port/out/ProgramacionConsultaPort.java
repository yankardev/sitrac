package com.yankardev.sitrac.viaje.domain.port.out;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProgramacionConsultaPort {

    Optional<ProgramacionOperacion> buscarProgramacion(Long id);

    void iniciarViaje(Long programacionId);

    void finalizarViaje(Long programacionId);

    void cancelarViaje(Long programacionId, boolean iniciado);

    record ProgramacionOperacion(
            Long id,
            Long pedidoId,
            Long conductorId,
            Long tractoId,
            Long semirremolqueId,
            LocalDateTime fechaProgramada,
            String estado
    ) {}
}
