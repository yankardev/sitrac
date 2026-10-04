package com.yankardev.sitrac.somma.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.somma.domain.model.EstadoRegistroSomma;
import com.yankardev.sitrac.somma.domain.model.TipoRegistroSomma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record RegistroSommaRequest(
        @NotNull TipoRegistroSomma tipo,
        Long programacionId,
        Long conductorId,
        @NotNull LocalDateTime fecha,
        @NotBlank @Size(max = 150) String titulo,
        @NotBlank @Size(max = 1000) String descripcion,
        @Size(max = 150) String lugar,
        EstadoRegistroSomma estado
) {}
