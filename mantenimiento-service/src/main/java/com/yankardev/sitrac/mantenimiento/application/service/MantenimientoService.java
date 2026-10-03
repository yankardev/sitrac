package com.yankardev.sitrac.mantenimiento.application.service;
import com.yankardev.sitrac.mantenimiento.application.exception.*;import com.yankardev.sitrac.mantenimiento.domain.model.*;import com.yankardev.sitrac.mantenimiento.domain.port.in.MantenimientoUseCase;import com.yankardev.sitrac.mantenimiento.domain.port.out.MantenimientoRepositoryPort;
import lombok.RequiredArgsConstructor;import org.springframework.stereotype.Service;import java.util.List;
@Service @RequiredArgsConstructor
public class MantenimientoService implements MantenimientoUseCase{
 private final MantenimientoRepositoryPort repo;
 public Mantenimiento crear(Mantenimiento m){validar(m);return repo.guardar(Mantenimiento.builder().tipoUnidad(m.getTipoUnidad()).unidadId(m.getUnidadId()).tipoMantenimiento(m.getTipoMantenimiento()).fechaInicio(m.getFechaInicio()).fechaFin(m.getFechaFin()).descripcion(m.getDescripcion()).costo(m.getCosto()).estado(EstadoMantenimiento.PROGRAMADO).build());}
 public List<Mantenimiento> listar(){return repo.listar();}
 public Mantenimiento obtenerPorId(Long id){return repo.buscarPorId(id).orElseThrow(()->new MantenimientoNoEncontradoException(id));}
 public Mantenimiento actualizar(Long id,Mantenimiento m){Mantenimiento a=obtenerPorId(id);validar(m);return repo.guardar(Mantenimiento.builder().id(a.getId()).tipoUnidad(m.getTipoUnidad()).unidadId(m.getUnidadId()).tipoMantenimiento(m.getTipoMantenimiento()).fechaInicio(m.getFechaInicio()).fechaFin(m.getFechaFin()).descripcion(m.getDescripcion()).costo(m.getCosto()).estado(m.getEstado()==null?a.getEstado():m.getEstado()).build());}
 public void eliminar(Long id){obtenerPorId(id);repo.eliminarPorId(id);}
 private void validar(Mantenimiento m){if(m.getFechaInicio()!=null&&m.getFechaFin()!=null&&m.getFechaFin().isBefore(m.getFechaInicio()))throw new ReglaNegocioException("La fecha fin no puede ser anterior a la fecha de inicio");}
}