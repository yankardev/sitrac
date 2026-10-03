package com.yankardev.sitrac.programacion.infrastructure.adapter.in.rest;
import com.yankardev.sitrac.programacion.domain.model.Programacion;import com.yankardev.sitrac.programacion.domain.port.in.ProgramacionUseCase;import com.yankardev.sitrac.programacion.infrastructure.adapter.in.rest.dto.*;
import jakarta.validation.Valid;import lombok.RequiredArgsConstructor;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/programaciones") @RequiredArgsConstructor
public class ProgramacionController{
 private final ProgramacionUseCase useCase;
 @PostMapping public ResponseEntity<ProgramacionResponse> crear(@Valid @RequestBody ProgramacionRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(resp(useCase.crear(dom(r))));}
 @GetMapping public List<ProgramacionResponse> listar(){return useCase.listar().stream().map(this::resp).toList();}
 @GetMapping("/{id}") public ProgramacionResponse obtener(@PathVariable Long id){return resp(useCase.obtenerPorId(id));}
 @PutMapping("/{id}") public ProgramacionResponse actualizar(@PathVariable Long id,@Valid @RequestBody ProgramacionRequest r){return resp(useCase.actualizar(id,dom(r)));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){useCase.eliminar(id);return ResponseEntity.noContent().build();}
 private Programacion dom(ProgramacionRequest r){return Programacion.builder().pedidoId(r.pedidoId()).conductorId(r.conductorId()).tractoId(r.tractoId()).semirremolqueId(r.semirremolqueId()).fechaProgramada(r.fechaProgramada()).observacion(r.observacion()).estado(r.estado()).build();}
 private ProgramacionResponse resp(Programacion p){return new ProgramacionResponse(p.getId(),p.getPedidoId(),p.getConductorId(),p.getTractoId(),p.getSemirremolqueId(),p.getFechaProgramada(),p.getObservacion(),p.getEstado());}
}