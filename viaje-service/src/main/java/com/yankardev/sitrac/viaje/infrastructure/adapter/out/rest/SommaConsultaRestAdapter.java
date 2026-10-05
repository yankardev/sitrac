package com.yankardev.sitrac.viaje.infrastructure.adapter.out.rest;

import com.yankardev.sitrac.viaje.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.viaje.domain.port.out.SommaConsultaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class SommaConsultaRestAdapter implements SommaConsultaPort {

    private final RestClient sommaClient;

    public SommaConsultaRestAdapter(@Value("${services.somma.url}") String sommaUrl) {
        this.sommaClient = RestClient.builder().baseUrl(sommaUrl).build();
    }

    @Override
    public boolean existeCharlaCerradaParaProgramacion(Long programacionId) {
        try {
            RegistroSommaResponse[] registros = sommaClient.get()
                    .uri("/api/somma")
                    .retrieve()
                    .body(RegistroSommaResponse[].class);

            if (registros == null) {
                return false;
            }

            for (RegistroSommaResponse registro : registros) {
                if ("CHARLA".equals(registro.tipo())
                        && programacionId.equals(registro.programacionId())
                        && "CERRADO".equals(registro.estado())) {
                    return true;
                }
            }

            return false;
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo consultar somma-service");
        }
    }

    private record RegistroSommaResponse(
            Long id,
            String tipo,
            Long programacionId,
            Long conductorId,
            String fecha,
            String titulo,
            String descripcion,
            String lugar,
            String estado
    ) {}
}
