package com.yankardev.sitrac.conductor.domain.port.in;

import com.yankardev.sitrac.conductor.domain.model.Conductor;

import java.util.List;

public interface ConductorUseCase {

    Conductor crear(Conductor conductor);

    List<Conductor> listar();

    Conductor obtenerPorId(Long id);

    Conductor actualizar(Long id, Conductor conductor);

    Conductor cambiarDisponibilidad(Long id, boolean disponible);

    void eliminar(Long id);
}
