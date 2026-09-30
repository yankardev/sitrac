package com.yankardev.sitrac.cliente.domain.port.out;

import com.yankardev.sitrac.cliente.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {

    Cliente guardar(Cliente cliente);

    List<Cliente> listar();

    Optional<Cliente> buscarPorId(Long id);

    boolean existePorNumeroDocumento(String numeroDocumento);

    boolean existePorNumeroDocumentoYIdDistinto(String numeroDocumento, Long id);

    void eliminarPorId(Long id);
}
