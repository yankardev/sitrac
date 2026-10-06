package com.yankardev.sitrac.viaje.domain.model;
import lombok.*;import java.math.BigDecimal;import java.time.LocalDateTime;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class Viaje{private Long id,programacionId;private LocalDateTime fechaInicio,fechaFin;private BigDecimal kilometrajeInicial,kilometrajeFinal;private String observacion;private EstadoViaje estado;}