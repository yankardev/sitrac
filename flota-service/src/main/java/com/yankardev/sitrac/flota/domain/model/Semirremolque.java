package com.yankardev.sitrac.flota.domain.model;
import lombok.*; import java.math.BigDecimal;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class Semirremolque {
    private Long id; private String placa; private TipoSemirremolque tipo; private BigDecimal capacidadToneladas;
    private EstadoUnidad estado; private boolean activo;
}