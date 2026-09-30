package com.yankardev.sitrac.cliente.application.service;

import com.yankardev.sitrac.cliente.application.exception.ClienteNoEncontradoException;
import com.yankardev.sitrac.cliente.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.cliente.domain.model.Cliente;
import com.yankardev.sitrac.cliente.domain.port.in.ClienteUseCase;
import com.yankardev.sitrac.cliente.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;

    @Override
    public Cliente crear(Cliente cliente) {
        if (clienteRepositoryPort.existePorNumeroDocumento(cliente.getNumeroDocumento())) {
            throw new ReglaNegocioException(
                    "Ya existe un cliente con el documento " + cliente.getNumeroDocumento()
            );
        }

        Cliente nuevoCliente = Cliente.builder()
                .tipoDocumento(cliente.getTipoDocumento())
                .numeroDocumento(cliente.getNumeroDocumento())
                .nombreRazonSocial(cliente.getNombreRazonSocial())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .direccion(cliente.getDireccion())
                .activo(true)
                .build();

        return clienteRepositoryPort.guardar(nuevoCliente);
    }

    @Override
    public List<Cliente> listar() {
        return clienteRepositoryPort.listar();
    }

    @Override
    public Cliente obtenerPorId(Long id) {
        return clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    @Override
    public Cliente actualizar(Long id, Cliente cliente) {
        Cliente actual = obtenerPorId(id);

        if (clienteRepositoryPort.existePorNumeroDocumentoYIdDistinto(
                cliente.getNumeroDocumento(), id)) {
            throw new ReglaNegocioException(
                    "Ya existe otro cliente con el documento " + cliente.getNumeroDocumento()
            );
        }

        Cliente actualizado = Cliente.builder()
                .id(actual.getId())
                .tipoDocumento(cliente.getTipoDocumento())
                .numeroDocumento(cliente.getNumeroDocumento())
                .nombreRazonSocial(cliente.getNombreRazonSocial())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .direccion(cliente.getDireccion())
                .activo(cliente.isActivo())
                .build();

        return clienteRepositoryPort.guardar(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        clienteRepositoryPort.eliminarPorId(id);
    }
}
