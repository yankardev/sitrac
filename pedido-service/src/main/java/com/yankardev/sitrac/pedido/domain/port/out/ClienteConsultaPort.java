package com.yankardev.sitrac.pedido.domain.port.out;

import java.util.Optional;

public interface ClienteConsultaPort {
    Optional<ClienteOperacion> buscarCliente(Long id);

    record ClienteOperacion(Long id, boolean activo) {}
}
