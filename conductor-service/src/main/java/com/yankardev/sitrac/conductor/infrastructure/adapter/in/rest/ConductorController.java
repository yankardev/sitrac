package com.yankardev.sitrac.conductor.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.conductor.domain.model.Conductor;
import com.yankardev.sitrac.conductor.domain.port.in.ConductorUseCase;
import com.yankardev.sitrac.conductor.infrastructure.adapter.in.rest.dto.ActualizarConductorRequest;
import com.yankardev.sitrac.conductor.infrastructure.adapter.in.rest.dto.ConductorResponse;
import com.yankardev.sitrac.conductor.infrastructure.adapter.in.rest.dto.CrearConductorRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conductores")
@RequiredArgsConstructor
public class ConductorController {

    private final ConductorUseCase conductorUseCase;

    @PostMapping
    public ResponseEntity<ConductorResponse> crear(
            @Valid @RequestBody CrearConductorRequest request) {

        Conductor creado = conductorUseCase.crear(Conductor.builder()
                .dni(request.dni())
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .numeroLicencia(request.numeroLicencia())
                .categoriaLicencia(request.categoriaLicencia())
                .fechaVencimientoLicencia(request.fechaVencimientoLicencia())
                .telefono(request.telefono())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creado));
    }

    @GetMapping
    public ResponseEntity<List<ConductorResponse>> listar() {
        List<ConductorResponse> conductores = conductorUseCase.listar()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(conductores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConductorResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(conductorUseCase.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConductorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarConductorRequest request) {

        Conductor actualizado = conductorUseCase.actualizar(id, Conductor.builder()
                .dni(request.dni())
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .numeroLicencia(request.numeroLicencia())
                .categoriaLicencia(request.categoriaLicencia())
                .fechaVencimientoLicencia(request.fechaVencimientoLicencia())
                .telefono(request.telefono())
                .disponible(request.disponible())
                .activo(request.activo())
                .build());

        return ResponseEntity.ok(toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        conductorUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private ConductorResponse toResponse(Conductor conductor) {
        return new ConductorResponse(
                conductor.getId(),
                conductor.getDni(),
                conductor.getNombres(),
                conductor.getApellidos(),
                conductor.getNumeroLicencia(),
                conductor.getCategoriaLicencia(),
                conductor.getFechaVencimientoLicencia(),
                conductor.getTelefono(),
                conductor.isDisponible(),
                conductor.isActivo()
        );
    }
}
