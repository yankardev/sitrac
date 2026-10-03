package com.yankardev.sitrac.programacion.infrastructure.adapter.out.persistence;
import com.yankardev.sitrac.programacion.domain.model.*;import com.yankardev.sitrac.programacion.domain.port.out.ProgramacionRepositoryPort;import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Component;import java.util.*;
@Component @RequiredArgsConstructor
public class ProgramacionPersistenceAdapter implements ProgramacionRepositoryPort{
 private final ProgramacionJpaRepository repo;
 public Programacion guardar(Programacion p){return d(repo.save(e(p)));}public List<Programacion> listar(){return repo.findAll().stream().map(this::d).toList();}public Optional<Programacion> buscarPorId(Long id){return repo.findById(id).map(this::d);}public boolean existePedidoConEstado(Long id,EstadoProgramacion est){return repo.existsByPedidoIdAndEstado(id,est);}public void eliminarPorId(Long id){repo.deleteById(id);}
 private ProgramacionJpaEntity e(Programacion p){return ProgramacionJpaEntity.builder().id(p.getId()).pedidoId(p.getPedidoId()).conductorId(p.getConductorId()).tractoId(p.getTractoId()).semirremolqueId(p.getSemirremolqueId()).fechaProgramada(p.getFechaProgramada()).observacion(p.getObservacion()).estado(p.getEstado()).build();}
 private Programacion d(ProgramacionJpaEntity e){return Programacion.builder().id(e.getId()).pedidoId(e.getPedidoId()).conductorId(e.getConductorId()).tractoId(e.getTractoId()).semirremolqueId(e.getSemirremolqueId()).fechaProgramada(e.getFechaProgramada()).observacion(e.getObservacion()).estado(e.getEstado()).build();}
}