package com.yankardev.sitrac.somma.infrastructure.adapter.out.rest;

import com.yankardev.sitrac.somma.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.somma.domain.port.out.ProgramacionConsultaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ProgramacionConsultaRestAdapter implements ProgramacionConsultaPort {

    private final RestClient programacionClient;

    public ProgramacionConsultaRestAdapter(
            @Value("${services.programacion.url}") String programacionUrl
    ) {
        this.programacionClient = RestClient.builder()
                .baseUrl(programacionUrl)
                .build();
    }

    @Override
    public Optional<ProgramacionOperacion> buscarProgramacion(Long id) {
        try {
            ProgramacionResponse response = programacionClient.get()
                    .uri("/api/programaciones/{id}", id)
                    .retrieve()
                    .body(ProgramacionResponse.class);

            return response == null
                    ? Optional.empty()
                    : Optional.of(new ProgramacionOperacion(
                            response.id(),
                            response.pedidoId(),
                            response.conductorId(),
                            response.tractoId(),
                            response.semirremolqueId(),
                            response.fechaProgramada(),
                            response.estado()
                    ));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo consultar programacion-service");
        }
    }

    private record ProgramacionResponse(
            Long id,
            Long pedidoId,
            Long conductorId,
            Long tractoId,
            Long semirremolqueId,
            LocalDateTime fechaProgramada,
            String observacion,
            String estado
    ) {}
}
