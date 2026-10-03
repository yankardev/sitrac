package com.yankardev.sitrac.combustible.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.combustible.domain.model.AbastecimientoCombustible;
import com.yankardev.sitrac.combustible.domain.port.in.AbastecimientoCombustibleUseCase;
import com.yankardev.sitrac.combustible.infrastructure.adapter.in.rest.dto.AbastecimientoRequest;
import com.yankardev.sitrac.combustible.infrastructure.adapter.in.rest.dto.AbastecimientoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/combustible/abastecimientos")
@RequiredArgsConstructor
public class AbastecimientoController {
    private final AbastecimientoCombustibleUseCase useCase;

    @PostMapping
    public ResponseEntity<AbastecimientoResponse> crear(@Valid @RequestBody AbastecimientoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(useCase.crear(toDomain(request))));
    }

    @GetMapping
    public List<AbastecimientoResponse> listar() {
        return useCase.listar().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public AbastecimientoResponse obtener(@PathVariable Long id) {
        return toResponse(useCase.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public AbastecimientoResponse actualizar(@PathVariable Long id, @Valid @RequestBody AbastecimientoRequest request) {
        return toResponse(useCase.actualizar(id, toDomain(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private AbastecimientoCombustible toDomain(AbastecimientoRequest r) {
        return AbastecimientoCombustible.builder()
                .programacionId(r.programacionId())
                .viajeId(r.viajeId())
                .conductorId(r.conductorId())
                .tractoId(r.tractoId())
                .tipoAbastecimiento(r.tipoAbastecimiento())
                .fechaHora(r.fechaHora())
                .cantidadGalones(r.cantidadGalones())
                .kilometraje(r.kilometraje())
                .precioUnitario(r.precioUnitario())
                .tanqueOrigen(r.tanqueOrigen())
                .proveedor(r.proveedor())
                .numeroComprobante(r.numeroComprobante())
                .observacion(r.observacion())
                .build();
    }

    private AbastecimientoResponse toResponse(AbastecimientoCombustible a) {
        return new AbastecimientoResponse(
                a.getId(), a.getProgramacionId(), a.getViajeId(), a.getConductorId(), a.getTractoId(),
                a.getTipoAbastecimiento(), a.getFechaHora(), a.getCantidadGalones(), a.getKilometraje(),
                a.getPrecioUnitario(), a.getCostoTotal(), a.getTanqueOrigen(), a.getProveedor(),
                a.getNumeroComprobante(), a.getObservacion()
        );
    }
}