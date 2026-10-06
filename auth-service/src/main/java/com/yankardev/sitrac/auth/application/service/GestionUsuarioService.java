package com.yankardev.sitrac.auth.application.service;

import com.yankardev.sitrac.auth.domain.model.Rol;
import com.yankardev.sitrac.auth.domain.model.Usuario;
import com.yankardev.sitrac.auth.domain.port.in.GestionUsuarioUseCase;
import com.yankardev.sitrac.auth.domain.port.out.UsuarioRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionUsuarioService implements GestionUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> listar() {
        return usuarioRepositoryPort.listar().stream()
                .sorted(Comparator.comparing(Usuario::getId).reversed())
                .toList();
    }

    @Override
    public Usuario actualizar(Long id, String username, String nombreCompleto, Rol rol) {
        Usuario actual = obtener(id);
        String usernameNormalizado = username.trim();

        if (!actual.getUsername().equalsIgnoreCase(usernameNormalizado)
                && usuarioRepositoryPort.existePorUsername(usernameNormalizado)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        return usuarioRepositoryPort.guardar(Usuario.builder()
                .id(actual.getId())
                .username(usernameNormalizado)
                .password(actual.getPassword())
                .nombreCompleto(nombreCompleto.trim())
                .rol(rol)
                .activo(actual.isActivo())
                .build());
    }

    @Override
    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario actual = obtener(id);

        return usuarioRepositoryPort.guardar(Usuario.builder()
                .id(actual.getId())
                .username(actual.getUsername())
                .password(actual.getPassword())
                .nombreCompleto(actual.getNombreCompleto())
                .rol(actual.getRol())
                .activo(activo)
                .build());
    }

    @Override
    public Usuario restablecerPassword(Long id, String nuevaPassword) {
        Usuario actual = obtener(id);

        return usuarioRepositoryPort.guardar(Usuario.builder()
                .id(actual.getId())
                .username(actual.getUsername())
                .password(passwordEncoder.encode(nuevaPassword))
                .nombreCompleto(actual.getNombreCompleto())
                .rol(actual.getRol())
                .activo(actual.isActivo())
                .build());
    }

    private Usuario obtener(Long id) {
        return usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
}
