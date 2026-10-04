package com.yankardev.sitrac.flota.domain.port.in;

import com.yankardev.sitrac.flota.domain.model.EstadoUnidad;
import com.yankardev.sitrac.flota.domain.model.Semirremolque;
import java.util.List;

public interface SemirremolqueUseCase {
    Semirremolque crear(Semirremolque s);
    List<Semirremolque> listar();
    Semirremolque obtenerPorId(Long id);
    Semirremolque actualizar(Long id, Semirremolque s);
    Semirremolque cambiarEstado(Long id, EstadoUnidad estado);
    void eliminar(Long id);
}
