package com.yankardev.sitrac.flota.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.flota.domain.model.Semirremolque;
import com.yankardev.sitrac.flota.domain.port.in.SemirremolqueUseCase;
import com.yankardev.sitrac.flota.infrastructure.adapter.in.rest.dto.EstadoUnidadRequest;
import com.yankardev.sitrac.flota.infrastructure.adapter.in.rest.dto.SemirremolqueRequest;
import com.yankardev.sitrac.flota.infrastructure.adapter.in.rest.dto.SemirremolqueResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semirremolques")
@RequiredArgsConstructor
public class SemirremolqueController {

    private final SemirremolqueUseCase useCase;

    @PostMapping
    public ResponseEntity<SemirremolqueResponse> crear(@Valid @RequestBody SemirremolqueRequest r) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(useCase.crear(toDomain(r, true))));
    }

    @GetMapping
    public List<SemirremolqueResponse> listar() {
        return useCase.listar().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public SemirremolqueResponse obtener(@PathVariable Long id) {
        return toResponse(useCase.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public SemirremolqueResponse actualizar(@PathVariable Long id, @Valid @RequestBody SemirremolqueRequest r) {
        return toResponse(useCase.actualizar(id, toDomain(r, false)));
    }

    @PutMapping("/{id}/estado")
    public SemirremolqueResponse cambiarEstado(@PathVariable Long id, @RequestBody EstadoUnidadRequest r) {
        return toResponse(useCase.cambiarEstado(id, r.estado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Semirremolque toDomain(SemirremolqueRequest r, boolean nuevo) {
        return Semirremolque.builder()
                .placa(r.placa().toUpperCase())
                .tipo(r.tipo())
                .capacidadToneladas(r.capacidadToneladas())
                .estado(r.estado())
                .activo(nuevo || r.activo() == null || r.activo())
                .build();
    }

    private SemirremolqueResponse toResponse(Semirremolque s) {
        return new SemirremolqueResponse(s.getId(), s.getPlaca(), s.getTipo(), s.getCapacidadToneladas(), s.getEstado(), s.isActivo());
    }
}
