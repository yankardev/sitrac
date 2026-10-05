package com.yankardev.sitrac.mantenimiento.domain.port.out;

import com.yankardev.sitrac.mantenimiento.domain.model.TipoUnidad;
import java.util.Optional;

public interface FlotaConsultaPort {
    Optional<UnidadFlota> buscarUnidad(TipoUnidad tipoUnidad, Long unidadId);
    void cambiarEstadoUnidad(TipoUnidad tipoUnidad, Long unidadId, String estado);

    record UnidadFlota(Long id, String estado, boolean activo) {}
}
