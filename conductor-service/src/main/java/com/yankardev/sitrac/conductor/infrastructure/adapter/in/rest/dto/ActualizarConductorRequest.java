package com.yankardev.sitrac.conductor.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ActualizarConductorRequest(
        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{8}", message = "El DNI debe contener 8 dígitos")
        String dni,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
        String apellidos,

        @NotBlank(message = "El número de licencia es obligatorio")
        @Size(max = 20, message = "El número de licencia no puede superar 20 caracteres")
        String numeroLicencia,

        @NotBlank(message = "La categoría de licencia es obligatoria")
        @Size(max = 20, message = "La categoría no puede superar 20 caracteres")
        String categoriaLicencia,

        @NotNull(message = "La fecha de vencimiento de la licencia es obligatoria")
        LocalDate fechaVencimientoLicencia,

        @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
        String telefono,

        boolean disponible,
        boolean activo
) {
}
