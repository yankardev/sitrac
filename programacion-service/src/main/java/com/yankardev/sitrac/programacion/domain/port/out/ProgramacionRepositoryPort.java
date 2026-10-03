package com.yankardev.sitrac.programacion.domain.port.out;
import com.yankardev.sitrac.programacion.domain.model.*;import java.util.*;
public interface ProgramacionRepositoryPort{Programacion guardar(Programacion p);List<Programacion> listar();Optional<Programacion> buscarPorId(Long id);boolean existePedidoConEstado(Long pedidoId,EstadoProgramacion estado);void eliminarPorId(Long id);}