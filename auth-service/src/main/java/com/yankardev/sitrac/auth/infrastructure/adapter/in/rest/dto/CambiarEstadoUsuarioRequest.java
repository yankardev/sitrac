package com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record CambiarEstadoUsuarioRequest(
        @NotNull(message = "El estado es obligatorio")
        Boolean activo
) {
}
