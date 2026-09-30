package com.yankardev.sitrac.cliente.infrastructure.adapter.in.rest.dto;

import com.yankardev.sitrac.cliente.domain.model.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearClienteRequest(
        @NotNull(message = "El tipo de documento es obligatorio")
        TipoDocumento tipoDocumento,

        @NotBlank(message = "El número de documento es obligatorio")
        @Size(min = 8, max = 20, message = "El número de documento debe tener entre 8 y 20 caracteres")
        String numeroDocumento,

        @NotBlank(message = "El nombre o razón social es obligatorio")
        @Size(min = 3, max = 150, message = "El nombre o razón social debe tener entre 3 y 150 caracteres")
        String nombreRazonSocial,

        @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
        String telefono,

        @Email(message = "El correo electrónico no tiene un formato válido")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres")
        String email,

        @Size(max = 200, message = "La dirección no puede superar 200 caracteres")
        String direccion
) {
}
