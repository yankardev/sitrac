package com.yankardev.sitrac.flota.infrastructure.adapter.in.rest.dto;
import com.yankardev.sitrac.flota.domain.model.EstadoUnidad; import java.math.BigDecimal;
public record TractoResponse(Long id,String placa,String marca,String modelo,Integer anio,BigDecimal capacidadToneladas,EstadoUnidad estado,boolean activo) {}