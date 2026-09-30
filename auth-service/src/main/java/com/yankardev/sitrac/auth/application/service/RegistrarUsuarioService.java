package com.yankardev.sitrac.auth.application.service;

import com.yankardev.sitrac.auth.domain.model.Rol;
import com.yankardev.sitrac.auth.domain.model.Usuario;
import com.yankardev.sitrac.auth.domain.port.in.RegistrarUsuarioUseCase;
import com.yankardev.sitrac.auth.domain.port.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrarUsuarioService implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario registrar(String username, String password, String nombreCompleto, Rol rol) {
        if (usuarioRepositoryPort.existePorUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        Usuario usuario = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nombreCompleto(nombreCompleto)
                .rol(rol)
                .activo(true)
                .build();

        return usuarioRepositoryPort.guardar(usuario);
    }
}
