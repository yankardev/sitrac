package com.yankardev.sitrac.operaciones.infrastructure;

import com.yankardev.sitrac.combustible.domain.port.out.ProgramacionConsultaPort;
import com.yankardev.sitrac.programacion.domain.model.Programacion;
import com.yankardev.sitrac.programacion.domain.port.in.ProgramacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class CombustibleProgramacionLocalAdapter implements ProgramacionConsultaPort {

    private final ProgramacionUseCase programacionUseCase;

    @Override
    public Optional<ProgramacionOperacion> buscarProgramacion(Long id) {
        try {
            Programacion p = programacionUseCase.obtenerPorId(id);
            return Optional.of(new ProgramacionOperacion(
                    p.getId(),
                    p.getConductorId(),
                    p.getTractoId(),
                    p.getEstado().name()
            ));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }
}
