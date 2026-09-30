package com.yankardev.sitrac.cliente.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.cliente.domain.model.Cliente;
import com.yankardev.sitrac.cliente.domain.port.out.ClienteRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository repository;

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteJpaEntity entity = toEntity(cliente);
        return toDomain(repository.save(entity));
    }

    @Override
    public List<Cliente> listar() {
        return repository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existePorNumeroDocumento(String numeroDocumento) {
        return repository.existsByNumeroDocumento(numeroDocumento);
    }

    @Override
    public boolean existePorNumeroDocumentoYIdDistinto(String numeroDocumento, Long id) {
        return repository.existsByNumeroDocumentoAndIdNot(numeroDocumento, id);
    }

    @Override
    public void eliminarPorId(Long id) {
        repository.deleteById(id);
    }

    private ClienteJpaEntity toEntity(Cliente cliente) {
        return ClienteJpaEntity.builder()
                .id(cliente.getId())
                .tipoDocumento(cliente.getTipoDocumento())
                .numeroDocumento(cliente.getNumeroDocumento())
                .nombreRazonSocial(cliente.getNombreRazonSocial())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .direccion(cliente.getDireccion())
                .activo(cliente.isActivo())
                .build();
    }

    private Cliente toDomain(ClienteJpaEntity entity) {
        return Cliente.builder()
                .id(entity.getId())
                .tipoDocumento(entity.getTipoDocumento())
                .numeroDocumento(entity.getNumeroDocumento())
                .nombreRazonSocial(entity.getNombreRazonSocial())
                .telefono(entity.getTelefono())
                .email(entity.getEmail())
                .direccion(entity.getDireccion())
                .activo(entity.isActivo())
                .build();
    }
}
