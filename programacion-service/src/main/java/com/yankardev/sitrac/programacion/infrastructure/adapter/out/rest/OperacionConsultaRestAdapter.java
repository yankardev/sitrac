package com.yankardev.sitrac.programacion.infrastructure.adapter.out.rest;

import com.yankardev.sitrac.programacion.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.programacion.domain.port.out.OperacionConsultaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class OperacionConsultaRestAdapter implements OperacionConsultaPort {

    private final RestClient pedidoClient;
    private final RestClient conductorClient;
    private final RestClient flotaClient;

    public OperacionConsultaRestAdapter(
            @Value("${services.pedido.url}") String pedidoUrl,
            @Value("${services.conductor.url}") String conductorUrl,
            @Value("${services.flota.url}") String flotaUrl
    ) {
        this.pedidoClient = RestClient.builder().baseUrl(pedidoUrl).build();
        this.conductorClient = RestClient.builder().baseUrl(conductorUrl).build();
        this.flotaClient = RestClient.builder().baseUrl(flotaUrl).build();
    }

    @Override
    public Optional<PedidoOperacion> buscarPedido(Long id) {
        try {
            PedidoResponse response = pedidoClient.get()
                    .uri("/api/pedidos/{id}", id)
                    .retrieve()
                    .body(PedidoResponse.class);

            return response == null
                    ? Optional.empty()
                    : Optional.of(new PedidoOperacion(
                            response.id(),
                            response.tipoCarga(),
                            response.toneladas(),
                            response.estado()
                    ));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo consultar pedido-service");
        }
    }

    @Override
    public void cambiarEstadoPedido(Long id, String estado) {
        try {
            pedidoClient.put()
                    .uri("/api/pedidos/{id}/estado", id)
                    .body(new EstadoPedidoRequest(estado))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo actualizar el estado del pedido");
        }
    }

    @Override
    public Optional<ConductorOperacion> buscarConductor(Long id) {
        try {
            ConductorResponse response = conductorClient.get()
                    .uri("/api/conductores/{id}", id)
                    .retrieve()
                    .body(ConductorResponse.class);

            return response == null
                    ? Optional.empty()
                    : Optional.of(new ConductorOperacion(
                            response.id(),
                            response.fechaVencimientoLicencia(),
                            response.disponible(),
                            response.activo()
                    ));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo consultar conductor-service");
        }
    }

    @Override
    public void cambiarDisponibilidadConductor(Long id, boolean disponible) {
        try {
            conductorClient.put()
                    .uri("/api/conductores/{id}/disponibilidad", id)
                    .body(new DisponibilidadConductorRequest(disponible))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo actualizar la disponibilidad del conductor");
        }
    }

    @Override
    public Optional<TractoOperacion> buscarTracto(Long id) {
        try {
            TractoResponse response = flotaClient.get()
                    .uri("/api/tractos/{id}", id)
                    .retrieve()
                    .body(TractoResponse.class);

            return response == null
                    ? Optional.empty()
                    : Optional.of(new TractoOperacion(
                            response.id(),
                            response.capacidadToneladas(),
                            response.estado(),
                            response.activo()
                    ));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo consultar flota-service para el tracto");
        }
    }

    @Override
    public void cambiarEstadoTracto(Long id, String estado) {
        try {
            flotaClient.put()
                    .uri("/api/tractos/{id}/estado", id)
                    .body(new EstadoUnidadRequest(estado))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo actualizar el estado del tracto");
        }
    }

    @Override
    public void cambiarEstadoSemirremolque(Long id, String estado) {
        try {
            flotaClient.put()
                    .uri("/api/semirremolques/{id}/estado", id)
                    .body(new EstadoUnidadRequest(estado))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo actualizar el estado del semirremolque");
        }
    }

    @Override
    public Optional<SemirremolqueOperacion> buscarSemirremolque(Long id) {
        try {
            SemirremolqueResponse response = flotaClient.get()
                    .uri("/api/semirremolques/{id}", id)
                    .retrieve()
                    .body(SemirremolqueResponse.class);

            return response == null
                    ? Optional.empty()
                    : Optional.of(new SemirremolqueOperacion(
                            response.id(),
                            response.tipo(),
                            response.capacidadToneladas(),
                            response.estado(),
                            response.activo()
                    ));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo consultar flota-service para el semirremolque");
        }
    }

    private record EstadoPedidoRequest(String estado) {}

    private record DisponibilidadConductorRequest(Boolean disponible) {}

    private record EstadoUnidadRequest(String estado) {}

    private record PedidoResponse(
            Long id,
            Long clienteId,
            String tipoCarga,
            String descripcionCarga,
            BigDecimal toneladas,
            String origen,
            String destino,
            LocalDate fechaSolicitud,
            String estado
    ) {}

    private record ConductorResponse(
            Long id,
            String dni,
            String nombres,
            String apellidos,
            String numeroLicencia,
            String categoriaLicencia,
            LocalDate fechaVencimientoLicencia,
            String telefono,
            boolean disponible,
            boolean activo
    ) {}

    private record TractoResponse(
            Long id,
            String placa,
            String marca,
            String modelo,
            Integer anio,
            BigDecimal capacidadToneladas,
            String estado,
            boolean activo
    ) {}

    private record SemirremolqueResponse(
            Long id,
            String placa,
            String tipo,
            BigDecimal capacidadToneladas,
            String estado,
            boolean activo
    ) {}
}
