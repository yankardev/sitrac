package com.yankardev.sitrac.combustible.solicitud;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/combustible/solicitudes")
public class SolicitudAbastecimientoController {

    private final SolicitudAbastecimientoService service;

    public SolicitudAbastecimientoController(SolicitudAbastecimientoService service) {
        this.service = service;
    }

    @GetMapping
    public List<SolicitudAbastecimiento> listar() {
        return service.listar();
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<SolicitudAbastecimiento> confirmar(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmarSolicitudRequest request
    ) {
        return ResponseEntity.ok(service.confirmar(
                id,
                request.puntoAbastecimiento(),
                request.cantidadGalones(),
                request.observacion()
        ));
    }

    @PostMapping("/{id}/notificar")
    public ResponseEntity<SolicitudAbastecimiento> notificar(@PathVariable Long id) {
        return ResponseEntity.ok(service.notificarConfirmada(id));
    }

    public record ConfirmarSolicitudRequest(
            @NotBlank(message = "El punto de abastecimiento es obligatorio") String puntoAbastecimiento,
            @NotNull(message = "La cantidad de galones es obligatoria")
            @DecimalMin(value = "0.001", message = "La cantidad de galones debe ser mayor que cero")
            BigDecimal cantidadGalones,
            String observacion
    ) {}
}
