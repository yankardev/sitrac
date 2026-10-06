package com.yankardev.sitrac.flota.application.service;

import com.yankardev.sitrac.flota.application.exception.RecursoNoEncontradoException;
import com.yankardev.sitrac.flota.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.flota.domain.model.EstadoUnidad;
import com.yankardev.sitrac.flota.domain.model.Tracto;
import com.yankardev.sitrac.flota.domain.port.in.TractoUseCase;
import com.yankardev.sitrac.flota.domain.port.out.TractoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TractoService implements TractoUseCase {

    private final TractoRepositoryPort repo;

    @Override
    public Tracto crear(Tracto t) {
        if (repo.existePorPlaca(t.getPlaca())) {
            throw new ReglaNegocioException("Ya existe un tracto con la placa " + t.getPlaca());
        }

        return repo.guardar(Tracto.builder()
                .placa(t.getPlaca())
                .marca(t.getMarca())
                .modelo(t.getModelo())
                .anio(t.getAnio())
                .capacidadToneladas(t.getCapacidadToneladas())
                .estado(EstadoUnidad.DISPONIBLE)
                .activo(true)
                .build());
    }

    @Override
    public List<Tracto> listar() {
        return repo.listar();
    }

    @Override
    public Tracto obtenerPorId(Long id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tracto", id));
    }

    @Override
    public Tracto actualizar(Long id, Tracto t) {
        Tracto actual = obtenerPorId(id);

        if (repo.existePorPlacaYIdDistinto(t.getPlaca(), id)) {
            throw new ReglaNegocioException("Ya existe otro tracto con la placa " + t.getPlaca());
        }

        return repo.guardar(Tracto.builder()
                .id(actual.getId())
                .placa(t.getPlaca())
                .marca(t.getMarca())
                .modelo(t.getModelo())
                .anio(t.getAnio())
                .capacidadToneladas(t.getCapacidadToneladas())
                .estado(t.getEstado() == null ? actual.getEstado() : t.getEstado())
                .activo(t.isActivo())
                .build());
    }

    @Override
    public Tracto cambiarEstado(Long id, EstadoUnidad estado) {
        Tracto actual = obtenerPorId(id);

        return repo.guardar(Tracto.builder()
                .id(actual.getId())
                .placa(actual.getPlaca())
                .marca(actual.getMarca())
                .modelo(actual.getModelo())
                .anio(actual.getAnio())
                .capacidadToneladas(actual.getCapacidadToneladas())
                .estado(estado)
                .activo(actual.isActivo())
                .build());
    }

    @Override
    public void eliminar(Long id) {
        Tracto actual = obtenerPorId(id);

        if (actual.getEstado() != EstadoUnidad.DISPONIBLE) {
            throw new ReglaNegocioException(
                    "Solo se puede eliminar un tracto disponible; no elimine unidades asignadas o en mantenimiento"
            );
        }

        repo.eliminarPorId(id);
    }
}
