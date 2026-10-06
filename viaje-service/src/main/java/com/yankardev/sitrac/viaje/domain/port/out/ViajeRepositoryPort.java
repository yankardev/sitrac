package com.yankardev.sitrac.viaje.domain.port.out;
import com.yankardev.sitrac.viaje.domain.model.Viaje;import java.util.*;
public interface ViajeRepositoryPort{Viaje guardar(Viaje v);List<Viaje> listar();Optional<Viaje> buscarPorId(Long id);boolean existePorProgramacionId(Long id);void eliminarPorId(Long id);}