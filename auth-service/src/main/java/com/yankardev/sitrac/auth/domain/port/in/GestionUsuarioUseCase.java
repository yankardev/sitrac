package com.yankardev.sitrac.auth.domain.port.in;

import com.yankardev.sitrac.auth.domain.model.Rol;
import com.yankardev.sitrac.auth.domain.model.Usuario;

import java.util.List;

public interface GestionUsuarioUseCase {

    List<Usuario> listar();

    Usuario actualizar(Long id, String username, String nombreCompleto, Rol rol);

    Usuario cambiarEstado(Long id, boolean activo);

    Usuario restablecerPassword(Long id, String nuevaPassword);
}
