package com.yankardev.sitrac.mantenimiento.infrastructure.adapter.out.rest;

import com.yankardev.sitrac.mantenimiento.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.mantenimiento.domain.model.TipoUnidad;
import com.yankardev.sitrac.mantenimiento.domain.port.out.FlotaConsultaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class FlotaConsultaRestAdapter implements FlotaConsultaPort {

    private final RestClient flotaClient;

    public FlotaConsultaRestAdapter(@Value("${services.flota.url}") String flotaUrl) {
        this.flotaClient = RestClient.builder().baseUrl(flotaUrl).build();
    }

    @Override
    public Optional<UnidadFlota> buscarUnidad(TipoUnidad tipoUnidad, Long unidadId) {
        String endpoint = tipoUnidad == TipoUnidad.TRACTO
                ? "/api/tractos/{id}"
                : "/api/semirremolques/{id}";

        try {
            UnidadResponse response = flotaClient.get()
                    .uri(endpoint, unidadId)
                    .retrieve()
                    .body(UnidadResponse.class);

            return response == null
                    ? Optional.empty()
                    : Optional.of(new UnidadFlota(response.id(), response.estado(), response.activo()));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo consultar flota-service");
        }
    }

    private record UnidadResponse(Long id, String estado, boolean activo) {}
}
