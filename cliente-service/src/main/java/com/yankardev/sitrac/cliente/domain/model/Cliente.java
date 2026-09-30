package com.yankardev.sitrac.cliente.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    private Long id;
    private TipoDocumento tipoDocumento;
    private String numeroDocumento;
    private String nombreRazonSocial;
    private String telefono;
    private String email;
    private String direccion;
    private boolean activo;
}
