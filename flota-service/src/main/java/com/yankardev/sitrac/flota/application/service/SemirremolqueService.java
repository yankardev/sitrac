package com.yankardev.sitrac.flota.application.service;

import com.yankardev.sitrac.flota.application.exception.RecursoNoEncontradoException;
import com.yankardev.sitrac.flota.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.flota.domain.model.EstadoUnidad;
import com.yankardev.sitrac.flota.domain.model.Semirremolque;
import com.yankardev.sitrac.flota.domain.port.in.SemirremolqueUseCase;
import com.yankardev.sitrac.flota.domain.port.out.SemirremolqueRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemirremolqueService implements SemirremolqueUseCase {

    private final SemirremolqueRepositoryPort repo;

    @Override
    public Semirremolque crear(Semirremolque s) {
        if (repo.existePorPlaca(s.getPlaca())) {
            throw new ReglaNegocioException("Ya existe un semirremolque con la placa " + s.getPlaca());
        }

        return repo.guardar(Semirremolque.builder()
                .placa(s.getPlaca())
                .tipo(s.getTipo())
                .capacidadToneladas(s.getCapacidadToneladas())
                .estado(EstadoUnidad.DISPONIBLE)
                .activo(true)
                .build());
    }

    @Override
    public List<Semirremolque> listar() {
        return repo.listar();
    }

    @Override
    public Semirremolque obtenerPorId(Long id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Semirremolque", id));
    }

    @Override
    public Semirremolque actualizar(Long id, Semirremolque s) {
        Semirremolque actual = obtenerPorId(id);

        if (repo.existePorPlacaYIdDistinto(s.getPlaca(), id)) {
            throw new ReglaNegocioException("Ya existe otro semirremolque con la placa " + s.getPlaca());
        }

        return repo.guardar(Semirremolque.builder()
                .id(actual.getId())
                .placa(s.getPlaca())
                .tipo(s.getTipo())
                .capacidadToneladas(s.getCapacidadToneladas())
                .estado(s.getEstado() == null ? actual.getEstado() : s.getEstado())
                .activo(s.isActivo())
                .build());
    }

    @Override
    public Semirremolque cambiarEstado(Long id, EstadoUnidad estado) {
        Semirremolque actual = obtenerPorId(id);

        return repo.guardar(Semirremolque.builder()
                .id(actual.getId())
                .placa(actual.getPlaca())
                .tipo(actual.getTipo())
                .capacidadToneladas(actual.getCapacidadToneladas())
                .estado(estado)
                .activo(actual.isActivo())
                .build());
    }

    @Override
    public void eliminar(Long id) {
        Semirremolque actual = obtenerPorId(id);

        if (actual.getEstado() != EstadoUnidad.DISPONIBLE) {
            throw new ReglaNegocioException(
                    "Solo se puede eliminar un semirremolque disponible; no elimine unidades asignadas o en mantenimiento"
            );
        }

        repo.eliminarPorId(id);
    }
}
