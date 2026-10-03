package com.yankardev.sitrac.viaje.application.service;
import com.yankardev.sitrac.viaje.application.exception.*;import com.yankardev.sitrac.viaje.domain.model.*;import com.yankardev.sitrac.viaje.domain.port.in.ViajeUseCase;import com.yankardev.sitrac.viaje.domain.port.out.ViajeRepositoryPort;
import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;import java.util.List;
@Service @RequiredArgsConstructor
public class ViajeService implements ViajeUseCase{
 private final ViajeRepositoryPort repo;
 public Viaje crear(Viaje v){if(repo.existePorProgramacionId(v.getProgramacionId()))throw new ReglaNegocioException("Ya existe un viaje para la programación "+v.getProgramacionId());validar(v);return repo.guardar(Viaje.builder().programacionId(v.getProgramacionId()).fechaInicio(v.getFechaInicio()).fechaFin(v.getFechaFin()).kilometrajeInicial(v.getKilometrajeInicial()).kilometrajeFinal(v.getKilometrajeFinal()).observacion(v.getObservacion()).estado(EstadoViaje.PROGRAMADO).build());}
 public List<Viaje> listar(){return repo.listar();}
 public Viaje obtenerPorId(Long id){return repo.buscarPorId(id).orElseThrow(()->new ViajeNoEncontradoException(id));}
 public Viaje actualizar(Long id,Viaje v){Viaje a=obtenerPorId(id);validar(v);return repo.guardar(Viaje.builder().id(a.getId()).programacionId(a.getProgramacionId()).fechaInicio(v.getFechaInicio()).fechaFin(v.getFechaFin()).kilometrajeInicial(v.getKilometrajeInicial()).kilometrajeFinal(v.getKilometrajeFinal()).observacion(v.getObservacion()).estado(v.getEstado()==null?a.getEstado():v.getEstado()).build());}
 public void eliminar(Long id){obtenerPorId(id);repo.eliminarPorId(id);}
 private void validar(Viaje v){if(v.getKilometrajeInicial()!=null&&v.getKilometrajeFinal()!=null&&v.getKilometrajeFinal().compareTo(v.getKilometrajeInicial())<0)throw new ReglaNegocioException("El kilometraje final no puede ser menor al inicial");}
}