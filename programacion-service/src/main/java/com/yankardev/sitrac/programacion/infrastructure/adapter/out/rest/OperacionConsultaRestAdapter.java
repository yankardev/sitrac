package com.yankardev.sitrac.programacion.infrastructure.adapter.out.rest;

import com.yankardev.sitrac.programacion.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.programacion.domain.port.out.OperacionConsultaPort;
import com.yankardev.sitrac.programacion.infrastructure.adapter.out.rest.client.ConductorFeignClient;
import com.yankardev.sitrac.programacion.infrastructure.adapter.out.rest.client.FlotaFeignClient;
import com.yankardev.sitrac.programacion.infrastructure.adapter.out.rest.client.PedidoFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OperacionConsultaRestAdapter implements OperacionConsultaPort {

    private final PedidoFeignClient pedidoClient;
    private final ConductorFeignClient conductorClient;
    private final FlotaFeignClient flotaClient;

    @Override
    public Optional<PedidoOperacion> buscarPedido(Long id) {
        try {
            PedidoFeignClient.PedidoResponse response = pedidoClient.obtener(id);
            return response == null
                    ? Optional.empty()
                    : Optional.of(new PedidoOperacion(
                            response.id(),
                            response.tipoCarga(),
                            response.toneladas(),
                            response.estado()
                    ));
        } catch (FeignException.NotFound ex) {
            return Optional.empty();
        } catch (FeignException ex) {
            throw new ReglaNegocioException("No se pudo consultar pedido-service");
        }
    }

    @Override
    public void cambiarEstadoPedido(Long id, String estado) {
        try {
            pedidoClient.cambiarEstado(id, new PedidoFeignClient.EstadoPedidoRequest(estado));
        } catch (FeignException ex) {
            throw new ReglaNegocioException("No se pudo actualizar el estado del pedido");
        }
    }

    @Override
    public Optional<ConductorOperacion> buscarConductor(Long id) {
        try {
            ConductorFeignClient.ConductorResponse response = conductorClient.obtener(id);
            return response == null
                    ? Optional.empty()
                    : Optional.of(new ConductorOperacion(
                            response.id(),
                            response.fechaVencimientoLicencia(),
                            response.disponible(),
                            response.activo()
                    ));
        } catch (FeignException.NotFound ex) {
            return Optional.empty();
        } catch (FeignException ex) {
            throw new ReglaNegocioException("No se pudo consultar conductor-service");
        }
    }

    @Override
    public void cambiarDisponibilidadConductor(Long id, boolean disponible) {
        try {
            conductorClient.cambiarDisponibilidad(id, new ConductorFeignClient.DisponibilidadRequest(disponible));
        } catch (FeignException ex) {
            throw new ReglaNegocioException("No se pudo actualizar la disponibilidad del conductor");
        }
    }

    @Override
    public Optional<TractoOperacion> buscarTracto(Long id) {
        try {
            FlotaFeignClient.TractoResponse response = flotaClient.obtenerTracto(id);
            return response == null
                    ? Optional.empty()
                    : Optional.of(new TractoOperacion(
                            response.id(),
                            response.capacidadToneladas(),
                            response.estado(),
                            response.activo()
                    ));
        } catch (FeignException.NotFound ex) {
            return Optional.empty();
        } catch (FeignException ex) {
            throw new ReglaNegocioException("No se pudo consultar flota-service para el tracto");
        }
    }

    @Override
    public void cambiarEstadoTracto(Long id, String estado) {
        try {
            flotaClient.cambiarEstadoTracto(id, new FlotaFeignClient.EstadoUnidadRequest(estado));
        } catch (FeignException ex) {
            throw new ReglaNegocioException("No se pudo actualizar el estado del tracto");
        }
    }

    @Override
    public Optional<SemirremolqueOperacion> buscarSemirremolque(Long id) {
        try {
            FlotaFeignClient.SemirremolqueResponse response = flotaClient.obtenerSemirremolque(id);
            return response == null
                    ? Optional.empty()
                    : Optional.of(new SemirremolqueOperacion(
                            response.id(),
                            response.tipo(),
                            response.capacidadToneladas(),
                            response.estado(),
                            response.activo()
                    ));
        } catch (FeignException.NotFound ex) {
            return Optional.empty();
        } catch (FeignException ex) {
            throw new ReglaNegocioException("No se pudo consultar flota-service para el semirremolque");
        }
    }

    @Override
    public void cambiarEstadoSemirremolque(Long id, String estado) {
        try {
            flotaClient.cambiarEstadoSemirremolque(id, new FlotaFeignClient.EstadoUnidadRequest(estado));
        } catch (FeignException ex) {
            throw new ReglaNegocioException("No se pudo actualizar el estado del semirremolque");
        }
    }
}
