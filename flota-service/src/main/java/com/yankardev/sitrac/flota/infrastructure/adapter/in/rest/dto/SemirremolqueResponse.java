package com.yankardev.sitrac.flota.infrastructure.adapter.in.rest.dto;
import com.yankardev.sitrac.flota.domain.model.*; import java.math.BigDecimal;
public record SemirremolqueResponse(Long id,String placa,TipoSemirremolque tipo,BigDecimal capacidadToneladas,EstadoUnidad estado,boolean activo) {}