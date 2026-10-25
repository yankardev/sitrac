package com.yankardev.sitrac.operaciones.infrastructure;

import com.yankardev.sitrac.programacion.domain.model.Programacion;
import com.yankardev.sitrac.programacion.domain.port.in.ProgramacionUseCase;
import com.yankardev.sitrac.viaje.domain.port.out.ProgramacionConsultaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class ViajeProgramacionLocalAdapter implements ProgramacionConsultaPort {

    private final ProgramacionUseCase programacionUseCase;

    @Override
    public Optional<ProgramacionOperacion> buscarProgramacion(Long id) {
        try {
            Programacion p = programacionUseCase.obtenerPorId(id);
            return Optional.of(new ProgramacionOperacion(
                    p.getId(),
                    p.getPedidoId(),
                    p.getConductorId(),
                    p.getTractoId(),
                    p.getSemirremolqueId(),
                    p.getFechaProgramada(),
                    p.getEstado().name()
            ));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    @Override
    public void iniciarViaje(Long programacionId) {
        programacionUseCase.iniciarViaje(programacionId);
    }

    @Override
    public void finalizarViaje(Long programacionId) {
        programacionUseCase.finalizarViaje(programacionId);
    }

    @Override
    public void cancelarViaje(Long programacionId, boolean iniciado) {
        programacionUseCase.cancelarPorViaje(programacionId, iniciado);
    }
}
