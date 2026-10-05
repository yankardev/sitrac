package com.yankardev.sitrac.mantenimiento.application.service;

import com.yankardev.sitrac.mantenimiento.application.exception.MantenimientoNoEncontradoException;
import com.yankardev.sitrac.mantenimiento.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.mantenimiento.domain.model.EstadoMantenimiento;
import com.yankardev.sitrac.mantenimiento.domain.model.Mantenimiento;
import com.yankardev.sitrac.mantenimiento.domain.model.TipoUnidad;
import com.yankardev.sitrac.mantenimiento.domain.port.in.MantenimientoUseCase;
import com.yankardev.sitrac.mantenimiento.domain.port.out.FlotaConsultaPort;
import com.yankardev.sitrac.mantenimiento.domain.port.out.MantenimientoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MantenimientoService implements MantenimientoUseCase {

    private final MantenimientoRepositoryPort repo;
    private final FlotaConsultaPort flotaConsulta;

    @Override
    public Mantenimiento crear(Mantenimiento mantenimiento) {
        validar(mantenimiento);
        validarUnidad(mantenimiento.getTipoUnidad(), mantenimiento.getUnidadId());

        return repo.guardar(Mantenimiento.builder()
                .tipoUnidad(mantenimiento.getTipoUnidad())
                .unidadId(mantenimiento.getUnidadId())
                .tipoMantenimiento(mantenimiento.getTipoMantenimiento())
                .fechaInicio(mantenimiento.getFechaInicio())
                .fechaFin(mantenimiento.getFechaFin())
                .descripcion(mantenimiento.getDescripcion())
                .costo(mantenimiento.getCosto())
                .estado(EstadoMantenimiento.PROGRAMADO)
                .build());
    }

    @Override
    public List<Mantenimiento> listar() {
        return repo.listar();
    }

    @Override
    public Mantenimiento obtenerPorId(Long id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new MantenimientoNoEncontradoException(id));
    }

    @Override
    public Mantenimiento actualizar(Long id, Mantenimiento mantenimiento) {
        Mantenimiento actual = obtenerPorId(id);
        validar(mantenimiento);
        validarUnidad(mantenimiento.getTipoUnidad(), mantenimiento.getUnidadId());

        EstadoMantenimiento nuevoEstado = mantenimiento.getEstado() == null
                ? actual.getEstado()
                : mantenimiento.getEstado();

        Mantenimiento actualizado = repo.guardar(Mantenimiento.builder()
                .id(actual.getId())
                .tipoUnidad(mantenimiento.getTipoUnidad())
                .unidadId(mantenimiento.getUnidadId())
                .tipoMantenimiento(mantenimiento.getTipoMantenimiento())
                .fechaInicio(mantenimiento.getFechaInicio())
                .fechaFin(mantenimiento.getFechaFin())
                .descripcion(mantenimiento.getDescripcion())
                .costo(mantenimiento.getCosto())
                .estado(nuevoEstado)
                .build());

        sincronizarEstadoFlota(actual, actualizado);
        return actualizado;
    }

    @Override
    public void eliminar(Long id) {
        Mantenimiento actual = obtenerPorId(id);
        repo.eliminarPorId(id);

        if (actual.getEstado() == EstadoMantenimiento.EN_PROCESO) {
            flotaConsulta.cambiarEstadoUnidad(actual.getTipoUnidad(), actual.getUnidadId(), "DISPONIBLE");
        }
    }

    private void sincronizarEstadoFlota(Mantenimiento anterior, Mantenimiento actual) {
        boolean cambioUnidad = anterior.getTipoUnidad() != actual.getTipoUnidad()
                || !anterior.getUnidadId().equals(actual.getUnidadId());

        if (cambioUnidad && anterior.getEstado() == EstadoMantenimiento.EN_PROCESO) {
            flotaConsulta.cambiarEstadoUnidad(anterior.getTipoUnidad(), anterior.getUnidadId(), "DISPONIBLE");
        }

        if (actual.getEstado() == EstadoMantenimiento.EN_PROCESO) {
            flotaConsulta.cambiarEstadoUnidad(actual.getTipoUnidad(), actual.getUnidadId(), "MANTENIMIENTO");
            return;
        }

        if (!cambioUnidad
                && anterior.getEstado() == EstadoMantenimiento.EN_PROCESO
                && (actual.getEstado() == EstadoMantenimiento.FINALIZADO
                || actual.getEstado() == EstadoMantenimiento.CANCELADO)) {
            flotaConsulta.cambiarEstadoUnidad(actual.getTipoUnidad(), actual.getUnidadId(), "DISPONIBLE");
        }
    }

    private void validar(Mantenimiento mantenimiento) {
        if (mantenimiento.getFechaInicio() != null
                && mantenimiento.getFechaFin() != null
                && mantenimiento.getFechaFin().isBefore(mantenimiento.getFechaInicio())) {
            throw new ReglaNegocioException("La fecha fin no puede ser anterior a la fecha de inicio");
        }
    }

    private void validarUnidad(TipoUnidad tipoUnidad, Long unidadId) {
        FlotaConsultaPort.UnidadFlota unidad = flotaConsulta.buscarUnidad(tipoUnidad, unidadId)
                .orElseThrow(() -> new ReglaNegocioException(
                        tipoUnidad == TipoUnidad.TRACTO
                                ? "El tracto indicado no existe"
                                : "El semirremolque indicado no existe"));

        if (!unidad.activo() || "INACTIVO".equals(unidad.estado())) {
            throw new ReglaNegocioException("La unidad de flota se encuentra inactiva");
        }
    }
}
