package com.yankardev.sitrac.cliente.domain.port.in;

import com.yankardev.sitrac.cliente.domain.model.Cliente;

import java.util.List;

public interface ClienteUseCase {

    Cliente crear(Cliente cliente);

    List<Cliente> listar();

    Cliente obtenerPorId(Long id);

    Cliente actualizar(Long id, Cliente cliente);

    void eliminar(Long id);
}
