package com.yankardev.sitrac.programacion.domain.port.out;

import com.yankardev.sitrac.programacion.domain.model.EstadoProgramacion;
import com.yankardev.sitrac.programacion.domain.model.Programacion;

import java.util.List;
import java.util.Optional;

public interface ProgramacionRepositoryPort {

    Programacion guardar(Programacion programacion);

    List<Programacion> listar();

    Optional<Programacion> buscarPorId(Long id);

    boolean existePedidoConEstado(Long pedidoId, EstadoProgramacion estado);

    boolean existeConductorConEstado(Long conductorId, EstadoProgramacion estado);

    boolean existeTractoConEstado(Long tractoId, EstadoProgramacion estado);

    boolean existeSemirremolqueConEstado(Long semirremolqueId, EstadoProgramacion estado);

    boolean existePedidoConEstadoExcepto(Long pedidoId, EstadoProgramacion estado, Long id);

    boolean existeConductorConEstadoExcepto(Long conductorId, EstadoProgramacion estado, Long id);

    boolean existeTractoConEstadoExcepto(Long tractoId, EstadoProgramacion estado, Long id);

    boolean existeSemirremolqueConEstadoExcepto(Long semirremolqueId, EstadoProgramacion estado, Long id);

    void eliminarPorId(Long id);
}
