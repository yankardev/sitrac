package com.yankardev.sitrac.auth.domain.port.in;

import com.yankardev.sitrac.auth.domain.model.Usuario;

public interface LoginUseCase {

    Usuario login(String username, String password);
}
