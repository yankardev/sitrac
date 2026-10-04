package com.yankardev.sitrac.somma.domain.port.out;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProgramacionConsultaPort {

    Optional<ProgramacionOperacion> buscarProgramacion(Long id);

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
