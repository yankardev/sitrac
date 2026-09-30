package com.yankardev.sitrac.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String username;
    private String password;
    private String nombreCompleto;
    private Rol rol;
    private boolean activo;
}
