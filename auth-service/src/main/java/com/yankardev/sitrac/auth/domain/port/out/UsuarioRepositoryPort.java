package com.yankardev.sitrac.auth.domain.port.out;

import com.yankardev.sitrac.auth.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> buscarPorUsername(String username);

    Usuario guardar(Usuario usuario);

    boolean existePorUsername(String username);
}
