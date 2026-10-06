package com.yankardev.sitrac.viaje.application.service;

import com.yankardev.sitrac.viaje.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.viaje.application.exception.ViajeNoEncontradoException;
import com.yankardev.sitrac.viaje.domain.model.EstadoViaje;
import com.yankardev.sitrac.viaje.domain.model.Viaje;
import com.yankardev.sitrac.viaje.domain.port.in.ViajeUseCase;
import com.yankardev.sitrac.viaje.domain.port.out.ProgramacionConsultaPort;
import com.yankardev.sitrac.viaje.domain.port.out.SommaConsultaPort;
import com.yankardev.sitrac.viaje.domain.port.out.ViajeRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViajeService implements ViajeUseCase {

    private final ViajeRepositoryPort repo;
    private final ProgramacionConsultaPort programacionConsulta;
    private final SommaConsultaPort sommaConsulta;

    @Override
    public Viaje crear(Viaje viaje) {
        validarProgramacion(viaje.getProgramacionId());

        if (repo.existePorProgramacionId(viaje.getProgramacionId())) {
            throw new ReglaNegocioException(
                    "Ya existe un viaje para la programación " + viaje.getProgramacionId()
            );
        }

        validarKilometraje(viaje);

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
        EstadoViaje nuevoEstado = viaje.getEstado() == null
                ? actual.getEstado()
                : viaje.getEstado();

        validarTransicion(actual.getEstado(), nuevoEstado);
        validarKilometraje(viaje);
        validarDatosPorEstado(nuevoEstado, viaje);

        boolean cambioEstado = actual.getEstado() != nuevoEstado;

        if (cambioEstado
                && actual.getEstado() == EstadoViaje.PROGRAMADO
                && nuevoEstado == EstadoViaje.EN_VIAJE) {
            validarCharlaSommaCerrada(actual.getProgramacionId());
            programacionConsulta.iniciarViaje(actual.getProgramacionId());
        }

        if (cambioEstado && nuevoEstado == EstadoViaje.FINALIZADO) {
            programacionConsulta.finalizarViaje(actual.getProgramacionId());
        }

        if (cambioEstado && nuevoEstado == EstadoViaje.CANCELADO) {
            programacionConsulta.cancelarViaje(
                    actual.getProgramacionId(),
                    actual.getEstado() == EstadoViaje.EN_VIAJE
            );
        }

        return repo.guardar(Viaje.builder()
                .id(actual.getId())
                .programacionId(actual.getProgramacionId())
                .fechaInicio(viaje.getFechaInicio())
                .fechaFin(viaje.getFechaFin())
                .kilometrajeInicial(viaje.getKilometrajeInicial())
                .kilometrajeFinal(viaje.getKilometrajeFinal())
                .observacion(viaje.getObservacion())
                .estado(nuevoEstado)
                .build());
    }

    @Override
    public void eliminar(Long id) {
        Viaje actual = obtenerPorId(id);

        if (actual.getEstado() != EstadoViaje.CANCELADO) {
            throw new ReglaNegocioException(
                    "Solo se puede eliminar un viaje cancelado; cancele el viaje para liberar correctamente la programación y sus recursos"
            );
        }

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

    private void validarCharlaSommaCerrada(Long programacionId) {
        if (!sommaConsulta.existeCharlaCerradaParaProgramacion(programacionId)) {
            throw new ReglaNegocioException(
                    "No se puede iniciar el viaje sin una charla SOMMA cerrada para la programación"
            );
        }
    }

    private void validarTransicion(EstadoViaje actual, EstadoViaje nuevo) {
        if (actual == nuevo) {
            return;
        }

        boolean permitida = switch (actual) {
            case PROGRAMADO -> nuevo == EstadoViaje.EN_VIAJE || nuevo == EstadoViaje.CANCELADO;
            case EN_VIAJE -> nuevo == EstadoViaje.FINALIZADO || nuevo == EstadoViaje.CANCELADO;
            case FINALIZADO, CANCELADO -> false;
        };

        if (!permitida) {
            throw new ReglaNegocioException(
                    "Transición de estado no permitida: " + actual + " -> " + nuevo
            );
        }
    }

    private void validarDatosPorEstado(EstadoViaje estado, Viaje viaje) {
        if (estado == EstadoViaje.EN_VIAJE) {
            if (viaje.getFechaInicio() == null) {
                throw new ReglaNegocioException(
                        "La fecha de inicio es obligatoria para iniciar el viaje"
                );
            }
            if (viaje.getKilometrajeInicial() == null) {
                throw new ReglaNegocioException(
                        "El kilometraje inicial es obligatorio para iniciar el viaje"
                );
            }
        }

        if (estado == EstadoViaje.FINALIZADO) {
            if (viaje.getFechaInicio() == null || viaje.getFechaFin() == null) {
                throw new ReglaNegocioException(
                        "Las fechas de inicio y fin son obligatorias para finalizar el viaje"
                );
            }
            if (viaje.getKilometrajeInicial() == null || viaje.getKilometrajeFinal() == null) {
                throw new ReglaNegocioException(
                        "Los kilometrajes inicial y final son obligatorios para finalizar el viaje"
                );
            }
            if (viaje.getFechaFin().isBefore(viaje.getFechaInicio())) {
                throw new ReglaNegocioException(
                        "La fecha de fin no puede ser anterior a la fecha de inicio"
                );
            }
        }
    }

    private void validarKilometraje(Viaje viaje) {
        if (viaje.getKilometrajeInicial() != null
                && viaje.getKilometrajeFinal() != null
                && viaje.getKilometrajeFinal().compareTo(viaje.getKilometrajeInicial()) < 0) {
            throw new ReglaNegocioException(
                    "El kilometraje final no puede ser menor al inicial"
            );
        }
    }
}
