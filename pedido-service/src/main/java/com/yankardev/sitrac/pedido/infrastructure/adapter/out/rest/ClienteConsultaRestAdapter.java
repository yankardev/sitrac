package com.yankardev.sitrac.pedido.infrastructure.adapter.out.rest;

import com.yankardev.sitrac.pedido.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.pedido.domain.port.out.ClienteConsultaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class ClienteConsultaRestAdapter implements ClienteConsultaPort {

    private final RestClient clienteClient;

    public ClienteConsultaRestAdapter(@Value("${services.cliente.url}") String clienteUrl) {
        this.clienteClient = RestClient.builder().baseUrl(clienteUrl).build();
    }

    @Override
    public Optional<ClienteOperacion> buscarCliente(Long id) {
        try {
            ClienteResponse response = clienteClient.get()
                    .uri("/api/clientes/{id}", id)
                    .retrieve()
                    .body(ClienteResponse.class);

            return response == null
                    ? Optional.empty()
                    : Optional.of(new ClienteOperacion(response.id(), response.activo()));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ReglaNegocioException("No se pudo consultar cliente-service");
        }
    }

    private record ClienteResponse(
            Long id,
            String tipoDocumento,
            String numeroDocumento,
            String nombreRazonSocial,
            String telefono,
            String email,
            String direccion,
            boolean activo
    ) {}
}
