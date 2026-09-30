package com.yankardev.sitrac.pedido.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.pedido.domain.model.Pedido;
import com.yankardev.sitrac.pedido.domain.port.in.PedidoUseCase;
import com.yankardev.sitrac.pedido.infrastructure.adapter.in.rest.dto.PedidoRequest;
import com.yankardev.sitrac.pedido.infrastructure.adapter.in.rest.dto.PedidoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoUseCase useCase;

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest r) {
        Pedido creado = useCase.crear(fromRequest(r));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creado));
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return useCase.listar().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@PathVariable Long id) {
        return toResponse(useCase.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public PedidoResponse actualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequest r) {
        return toResponse(useCase.actualizar(id, fromRequest(r)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private Pedido fromRequest(PedidoRequest r) {
        return Pedido.builder()
                .clienteId(r.clienteId()).tipoCarga(r.tipoCarga()).descripcionCarga(r.descripcionCarga())
                .toneladas(r.toneladas()).origen(r.origen()).destino(r.destino())
                .fechaSolicitud(r.fechaSolicitud()).estado(r.estado()).build();
    }

    private PedidoResponse toResponse(Pedido p) {
        return new PedidoResponse(p.getId(), p.getClienteId(), p.getTipoCarga(), p.getDescripcionCarga(),
                p.getToneladas(), p.getOrigen(), p.getDestino(), p.getFechaSolicitud(), p.getEstado());
    }
}
