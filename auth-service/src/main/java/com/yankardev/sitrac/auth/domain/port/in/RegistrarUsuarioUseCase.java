package com.yankardev.sitrac.auth.domain.port.in;

import com.yankardev.sitrac.auth.domain.model.Rol;
import com.yankardev.sitrac.auth.domain.model.Usuario;

public interface RegistrarUsuarioUseCase {

    Usuario registrar(String username, String password, String nombreCompleto, Rol rol);
}
