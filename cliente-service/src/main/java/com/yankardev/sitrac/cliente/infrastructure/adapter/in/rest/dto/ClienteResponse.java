package com.yankardev.sitrac.cliente.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.cliente.domain.model.TipoDocumento;

public record ClienteResponse(
        Long id,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombreRazonSocial,
        String telefono,
        String email,
        String direccion,
        boolean activo
) {
}
