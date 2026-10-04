package com.yankardev.sitrac.combustible.domain.port.out;

import java.util.Optional;

public interface ProgramacionConsultaPort {
    Optional<ProgramacionOperacion> buscarProgramacion(Long id);

    record ProgramacionOperacion(Long id, Long conductorId, Long tractoId, String estado) {}
}
