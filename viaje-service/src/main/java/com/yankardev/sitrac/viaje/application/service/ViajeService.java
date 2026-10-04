package com.yankardev.sitrac.viaje.application.service;

import com.yankardev.sitrac.viaje.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.viaje.application.exception.ViajeNoEncontradoException;
import com.yankardev.sitrac.viaje.domain.model.EstadoViaje;
import com.yankardev.sitrac.viaje.domain.model.Viaje;
import com.yankardev.sitrac.viaje.domain.port.in.ViajeUseCase;
import com.yankardev.sitrac.viaje.domain.port.out.ProgramacionConsultaPort;
import com.yankardev.sitrac.viaje.domain.port.out.ViajeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViajeService implements ViajeUseCase {

    private final ViajeRepositoryPort repo;
    private final ProgramacionConsultaPort programacionConsulta;

    @Override
    public Viaje crear(Viaje viaje) {
        validarProgramacion(viaje.getProgramacionId());

        if (repo.existePorProgramacionId(viaje.getProgramacionId())) {
            throw new ReglaNegocioException(
                    "Ya existe un viaje para la programación " + viaje.getProgramacionId()
            );
        }

        validar(viaje);

        return repo.guardar(Viaje.builder()
                .programacionId(viaje.getProgramacionId())
                .fechaInicio(viaje.getFechaInicio())
                .fechaFin(viaje.getFechaFin())
                .kilometrajeInicial(viaje.getKilometrajeInicial())
                .kilometrajeFinal(viaje.getKilometrajeFinal())
                .observacion(viaje.getObservacion())
                .estado(EstadoViaje.PROGRAMADO)
                .build());
    }

    @Override
    public List<Viaje> listar() {
        return repo.listar();
    }

    @Override
    public Viaje obtenerPorId(Long id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new ViajeNoEncontradoException(id));
    }

    @Override
    public Viaje actualizar(Long id, Viaje viaje) {
        Viaje actual = obtenerPorId(id);
        validar(viaje);

        return repo.guardar(Viaje.builder()
                .id(actual.getId())
                .programacionId(actual.getProgramacionId())
                .fechaInicio(viaje.getFechaInicio())
                .fechaFin(viaje.getFechaFin())
                .kilometrajeInicial(viaje.getKilometrajeInicial())
                .kilometrajeFinal(viaje.getKilometrajeFinal())
                .observacion(viaje.getObservacion())
                .estado(viaje.getEstado() == null ? actual.getEstado() : viaje.getEstado())
                .build());
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        repo.eliminarPorId(id);
    }

    private void validarProgramacion(Long programacionId) {
        ProgramacionConsultaPort.ProgramacionOperacion programacion =
                programacionConsulta.buscarProgramacion(programacionId)
                        .orElseThrow(() -> new ReglaNegocioException(
                                "La programación indicada no existe"
                        ));

        if (!"PROGRAMADA".equals(programacion.estado())) {
            throw new ReglaNegocioException(
                    "La programación debe estar en estado PROGRAMADA para generar el viaje"
            );
        }
    }

    private void validar(Viaje viaje) {
        if (viaje.getKilometrajeInicial() != null
                && viaje.getKilometrajeFinal() != null
                && viaje.getKilometrajeFinal().compareTo(viaje.getKilometrajeInicial()) < 0) {
            throw new ReglaNegocioException(
                    "El kilometraje final no puede ser menor al inicial"
            );
        }
    }
}
