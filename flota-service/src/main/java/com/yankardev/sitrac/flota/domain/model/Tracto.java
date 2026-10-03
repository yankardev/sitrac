package com.yankardev.sitrac.flota.domain.model;
import lombok.*; import java.math.BigDecimal;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class Tracto {
    private Long id; private String placa; private String marca; private String modelo; private Integer anio;
    private BigDecimal capacidadToneladas; private EstadoUnidad estado; private boolean activo;
}