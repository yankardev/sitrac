package com.yankardev.sitrac.somma.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroSomma {
    private Long id;
    private Long programacionId;
    private Long conductorId;
    private TipoRegistroSomma tipo;
    private LocalDateTime fecha;
    private String titulo;
    private String descripcion;
    private String lugar;
    private EstadoRegistroSomma estado;
}
