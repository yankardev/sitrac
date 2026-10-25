package com.yankardev.sitrac.recursos.infrastructure;

import com.yankardev.sitrac.flota.domain.model.EstadoUnidad;
import com.yankardev.sitrac.flota.domain.port.in.SemirremolqueUseCase;
import com.yankardev.sitrac.flota.domain.port.in.TractoUseCase;
import com.yankardev.sitrac.mantenimiento.domain.model.TipoUnidad;
import com.yankardev.sitrac.mantenimiento.domain.port.out.FlotaConsultaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class FlotaConsultaLocalAdapter implements FlotaConsultaPort {

    private final TractoUseCase tractoUseCase;
    private final SemirremolqueUseCase semirremolqueUseCase;

    @Override
    public Optional<UnidadFlota> buscarUnidad(TipoUnidad tipoUnidad, Long unidadId) {
        try {
            if (tipoUnidad == TipoUnidad.TRACTO) {
                var tracto = tractoUseCase.obtenerPorId(unidadId);
                return Optional.of(new UnidadFlota(tracto.getId(), tracto.getEstado().name(), tracto.isActivo()));
            }
            var semi = semirremolqueUseCase.obtenerPorId(unidadId);
            return Optional.of(new UnidadFlota(semi.getId(), semi.getEstado().name(), semi.isActivo()));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    @Override
    public void cambiarEstadoUnidad(TipoUnidad tipoUnidad, Long unidadId, String estado) {
        EstadoUnidad nuevoEstado = EstadoUnidad.valueOf(estado);
        if (tipoUnidad == TipoUnidad.TRACTO) {
            tractoUseCase.cambiarEstado(unidadId, nuevoEstado);
        } else {
            semirremolqueUseCase.cambiarEstado(unidadId, nuevoEstado);
        }
    }
}
