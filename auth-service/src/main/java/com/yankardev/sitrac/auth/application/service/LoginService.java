package com.yankardev.sitrac.auth.application.service;

import com.yankardev.sitrac.auth.domain.model.Usuario;
import com.yankardev.sitrac.auth.domain.port.in.LoginUseCase;
import com.yankardev.sitrac.auth.domain.port.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario login(String username, String password) {
        Usuario usuario = usuarioRepositoryPort.buscarPorUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        return usuario;
    }
}
