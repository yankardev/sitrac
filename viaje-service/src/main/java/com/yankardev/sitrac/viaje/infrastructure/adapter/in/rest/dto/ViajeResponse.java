package com.yankardev.sitrac.viaje.infrastructure.adapter.in.rest.dto;
import com.yankardev.sitrac.viaje.domain.model.EstadoViaje;import java.math.BigDecimal;import java.time.LocalDateTime;
public record ViajeResponse(Long id,Long programacionId,LocalDateTime fechaInicio,LocalDateTime fechaFin,BigDecimal kilometrajeInicial,BigDecimal kilometrajeFinal,String observacion,EstadoViaje estado){}