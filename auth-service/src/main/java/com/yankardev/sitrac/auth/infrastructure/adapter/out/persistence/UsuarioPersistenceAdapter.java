package com.yankardev.sitrac.auth.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.auth.domain.model.Usuario;
import com.yankardev.sitrac.auth.domain.port.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository repository;

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        return repository.findByUsername(username)
                .map(this::toDomain);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioJpaEntity entity = UsuarioJpaEntity.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .nombreCompleto(usuario.getNombreCompleto())
                .rol(usuario.getRol())
                .activo(usuario.isActivo())
                .build();

        return toDomain(repository.save(entity));
    }

    @Override
    public boolean existePorUsername(String username) {
        return repository.existsByUsername(username);
    }

    private Usuario toDomain(UsuarioJpaEntity entity) {
        return Usuario.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .nombreCompleto(entity.getNombreCompleto())
                .rol(entity.getRol())
                .activo(entity.isActivo())
                .build();
    }
}
