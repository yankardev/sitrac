package com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.auth.domain.model.Rol;

public record LoginResponse(
        Long id,
        String username,
        String nombreCompleto,
        Rol rol
) {
}
