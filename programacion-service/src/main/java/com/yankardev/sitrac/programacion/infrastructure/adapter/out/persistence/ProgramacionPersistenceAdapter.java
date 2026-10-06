package com.yankardev.sitrac.programacion.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.programacion.domain.model.EstadoProgramacion;
import com.yankardev.sitrac.programacion.domain.model.Programacion;
import com.yankardev.sitrac.programacion.domain.port.out.ProgramacionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProgramacionPersistenceAdapter implements ProgramacionRepositoryPort {

    private final ProgramacionJpaRepository repo;

    @Override
    public Programacion guardar(Programacion p) {
        return toDomain(repo.save(toEntity(p)));
    }

    @Override
    public List<Programacion> listar() {
        return repo.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Programacion> buscarPorId(Long id) {
        return repo.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existePedidoConEstado(Long pedidoId, EstadoProgramacion estado) {
        return repo.existsByPedidoIdAndEstado(pedidoId, estado);
    }

    @Override
    public boolean existeConductorConEstado(Long conductorId, EstadoProgramacion estado) {
        return repo.existsByConductorIdAndEstado(conductorId, estado);
    }

    @Override
    public boolean existeTractoConEstado(Long tractoId, EstadoProgramacion estado) {
        return repo.existsByTractoIdAndEstado(tractoId, estado);
    }

    @Override
    public boolean existeSemirremolqueConEstado(Long semirremolqueId, EstadoProgramacion estado) {
        return repo.existsBySemirremolqueIdAndEstado(semirremolqueId, estado);
    }

    @Override
    public boolean existePedidoConEstadoExcepto(Long pedidoId, EstadoProgramacion estado, Long id) {
        return repo.existsByPedidoIdAndEstadoAndIdNot(pedidoId, estado, id);
    }

    @Override
    public boolean existeConductorConEstadoExcepto(Long conductorId, EstadoProgramacion estado, Long id) {
        return repo.existsByConductorIdAndEstadoAndIdNot(conductorId, estado, id);
    }

    @Override
    public boolean existeTractoConEstadoExcepto(Long tractoId, EstadoProgramacion estado, Long id) {
        return repo.existsByTractoIdAndEstadoAndIdNot(tractoId, estado, id);
    }

    @Override
    public boolean existeSemirremolqueConEstadoExcepto(Long semirremolqueId, EstadoProgramacion estado, Long id) {
        return repo.existsBySemirremolqueIdAndEstadoAndIdNot(semirremolqueId, estado, id);
    }

    @Override
    public void eliminarPorId(Long id) {
        repo.deleteById(id);
    }

    private ProgramacionJpaEntity toEntity(Programacion p) {
        return ProgramacionJpaEntity.builder()
                .id(p.getId())
                .pedidoId(p.getPedidoId())
                .conductorId(p.getConductorId())
                .tractoId(p.getTractoId())
                .semirremolqueId(p.getSemirremolqueId())
                .fechaProgramada(p.getFechaProgramada())
                .observacion(p.getObservacion())
                .estado(p.getEstado())
                .build();
    }

    private Programacion toDomain(ProgramacionJpaEntity e) {
        return Programacion.builder()
                .id(e.getId())
                .pedidoId(e.getPedidoId())
                .conductorId(e.getConductorId())
                .tractoId(e.getTractoId())
                .semirremolqueId(e.getSemirremolqueId())
                .fechaProgramada(e.getFechaProgramada())
                .observacion(e.getObservacion())
                .estado(e.getEstado())
                .build();
    }
}
