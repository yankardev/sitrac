package com.yankardev.sitrac.programacion.application.service;
import com.yankardev.sitrac.programacion.application.exception.*;import com.yankardev.sitrac.programacion.domain.model.*;import com.yankardev.sitrac.programacion.domain.port.in.ProgramacionUseCase;import com.yankardev.sitrac.programacion.domain.port.out.ProgramacionRepositoryPort;
import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;import java.util.List;
@Service @RequiredArgsConstructor
public class ProgramacionService implements ProgramacionUseCase{
 private final ProgramacionRepositoryPort repo;
 public Programacion crear(Programacion p){if(repo.existePedidoConEstado(p.getPedidoId(),EstadoProgramacion.PROGRAMADA))throw new ReglaNegocioException("El pedido ya tiene una programación activa");return repo.guardar(Programacion.builder().pedidoId(p.getPedidoId()).conductorId(p.getConductorId()).tractoId(p.getTractoId()).semirremolqueId(p.getSemirremolqueId()).fechaProgramada(p.getFechaProgramada()).observacion(p.getObservacion()).estado(EstadoProgramacion.PROGRAMADA).build());}
 public List<Programacion> listar(){return repo.listar();}
 public Programacion obtenerPorId(Long id){return repo.buscarPorId(id).orElseThrow(()->new ProgramacionNoEncontradaException(id));}
 public Programacion actualizar(Long id,Programacion p){Programacion a=obtenerPorId(id);return repo.guardar(Programacion.builder().id(a.getId()).pedidoId(p.getPedidoId()).conductorId(p.getConductorId()).tractoId(p.getTractoId()).semirremolqueId(p.getSemirremolqueId()).fechaProgramada(p.getFechaProgramada()).observacion(p.getObservacion()).estado(p.getEstado()==null?a.getEstado():p.getEstado()).build());}
 public void eliminar(Long id){obtenerPorId(id);repo.eliminarPorId(id);}
}