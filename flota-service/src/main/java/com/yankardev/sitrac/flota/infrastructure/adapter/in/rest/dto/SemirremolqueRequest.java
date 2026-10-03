package com.yankardev.sitrac.flota.infrastructure.adapter.in.rest.dto;
import com.yankardev.sitrac.flota.domain.model.*; import jakarta.validation.constraints.*; import java.math.BigDecimal;
public record SemirremolqueRequest(@NotBlank String placa,@NotNull TipoSemirremolque tipo,@NotNull @DecimalMin("0.01") BigDecimal capacidadToneladas,EstadoUnidad estado,Boolean activo) {}