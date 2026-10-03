package com.yankardev.sitrac.programacion.domain.port.in;
import com.yankardev.sitrac.programacion.domain.model.Programacion;import java.util.List;
public interface ProgramacionUseCase{Programacion crear(Programacion p);List<Programacion> listar();Programacion obtenerPorId(Long id);Programacion actualizar(Long id,Programacion p);void eliminar(Long id);}