package com.yankardev.sitrac.comercial.infrastructure;

import com.yankardev.sitrac.cliente.domain.port.out.ClienteRepositoryPort;
import com.yankardev.sitrac.pedido.domain.port.out.ClienteConsultaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class ClienteConsultaLocalAdapter implements ClienteConsultaPort {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public Optional<ClienteOperacion> buscarCliente(Long id) {
        return clienteRepositoryPort.buscarPorId(id)
                .map(cliente -> new ClienteOperacion(cliente.getId(), cliente.isActivo()));
    }
}
