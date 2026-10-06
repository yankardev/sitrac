package com.yankardev.sitrac.auth.domain.port.out;

import com.yankardev.sitrac.auth.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorUsername(String username);

    List<Usuario> listar();

    Usuario guardar(Usuario usuario);

    boolean existePorUsername(String username);
}
