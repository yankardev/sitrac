package com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.auth.domain.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
        String password,

        @NotBlank(message = "El nombre completo es obligatorio")
        String nombreCompleto,

        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {
}
