package com.yankardev.sitrac.flota.domain.port.in;
import com.yankardev.sitrac.flota.domain.model.Tracto; import java.util.List;
public interface TractoUseCase { Tracto crear(Tracto t); List<Tracto> listar(); Tracto obtenerPorId(Long id); Tracto actualizar(Long id,Tracto t); void eliminar(Long id); }