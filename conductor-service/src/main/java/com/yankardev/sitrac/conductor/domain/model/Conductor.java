package com.yankardev.sitrac.conductor.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conductor {

    private Long id;
    private String dni;
    private String nombres;
    private String apellidos;
    private String numeroLicencia;
    private String categoriaLicencia;
    private LocalDate fechaVencimientoLicencia;
    private String telefono;
    private boolean disponible;
    private boolean activo;
}
