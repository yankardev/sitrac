package com.yankardev.sitrac.programacion.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.programacion.domain.model.Programacion;
import com.yankardev.sitrac.programacion.domain.port.in.ProgramacionUseCase;
import com.yankardev.sitrac.programacion.infrastructure.adapter.in.rest.dto.ProgramacionRequest;
import com.yankardev.sitrac.programacion.infrastructure.adapter.in.rest.dto.ProgramacionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programaciones")
@RequiredArgsConstructor
public class ProgramacionController {

    private final ProgramacionUseCase useCase;

    @PostMapping
    public ResponseEntity<ProgramacionResponse> crear(@Valid @RequestBody ProgramacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resp(useCase.crear(dom(request))));
    }

    @GetMapping
    public List<ProgramacionResponse> listar() {
        return useCase.listar().stream().map(this::resp).toList();
    }

    @GetMapping("/{id}")
    public ProgramacionResponse obtener(@PathVariable Long id) {
        return resp(useCase.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ProgramacionResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProgramacionRequest request
    ) {
        return resp(useCase.actualizar(id, dom(request)));
    }

    @PutMapping("/{id}/viaje/iniciar")
    public ProgramacionResponse iniciarViaje(@PathVariable Long id) {
        return resp(useCase.iniciarViaje(id));
    }

    @PutMapping("/{id}/viaje/finalizar")
    public ProgramacionResponse finalizarViaje(@PathVariable Long id) {
        return resp(useCase.finalizarViaje(id));
    }

    @PutMapping("/{id}/viaje/cancelar")
    public ProgramacionResponse cancelarPorViaje(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean iniciado
    ) {
        return resp(useCase.cancelarPorViaje(id, iniciado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Programacion dom(ProgramacionRequest request) {
        return Programacion.builder()
                .pedidoId(request.pedidoId())
                .conductorId(request.conductorId())
                .tractoId(request.tractoId())
                .semirremolqueId(request.semirremolqueId())
                .fechaProgramada(request.fechaProgramada())
                .observacion(request.observacion())
                .estado(request.estado())
                .build();
    }

    private ProgramacionResponse resp(Programacion programacion) {
        return new ProgramacionResponse(
                programacion.getId(),
                programacion.getPedidoId(),
                programacion.getConductorId(),
                programacion.getTractoId(),
                programacion.getSemirremolqueId(),
                programacion.getFechaProgramada(),
                programacion.getObservacion(),
                programacion.getEstado()
        );
    }
}
