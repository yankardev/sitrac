package com.yankardev.sitrac.somma.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.somma.domain.model.RegistroSomma;
import com.yankardev.sitrac.somma.domain.port.in.RegistroSommaUseCase;
import com.yankardev.sitrac.somma.infrastructure.adapter.in.rest.dto.RegistroSommaRequest;
import com.yankardev.sitrac.somma.infrastructure.adapter.in.rest.dto.RegistroSommaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/somma")
@RequiredArgsConstructor
public class RegistroSommaController {

    private final RegistroSommaUseCase useCase;

    @PostMapping
    public ResponseEntity<RegistroSommaResponse> crear(@Valid @RequestBody RegistroSommaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(useCase.crear(toDomain(request))));
    }

    @GetMapping
    public List<RegistroSommaResponse> listar() {
        return useCase.listar().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public RegistroSommaResponse obtener(@PathVariable Long id) {
        return toResponse(useCase.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public RegistroSommaResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RegistroSommaRequest request
    ) {
        return toResponse(useCase.actualizar(id, toDomain(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private RegistroSomma toDomain(RegistroSommaRequest request) {
        return RegistroSomma.builder()
                .tipo(request.tipo())
                .programacionId(request.programacionId())
                .conductorId(request.conductorId())
                .fecha(request.fecha())
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .lugar(request.lugar())
                .estado(request.estado())
                .build();
    }

    private RegistroSommaResponse toResponse(RegistroSomma registro) {
        return new RegistroSommaResponse(
                registro.getId(),
                registro.getTipo(),
                registro.getProgramacionId(),
                registro.getConductorId(),
                registro.getFecha(),
                registro.getTitulo(),
                registro.getDescripcion(),
                registro.getLugar(),
                registro.getEstado()
        );
    }
}
