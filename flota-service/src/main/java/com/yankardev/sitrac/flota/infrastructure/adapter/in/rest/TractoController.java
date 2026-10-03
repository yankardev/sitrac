package com.yankardev.sitrac.flota.infrastructure.adapter.in.rest;
import com.yankardev.sitrac.flota.domain.model.Tracto; import com.yankardev.sitrac.flota.domain.port.in.TractoUseCase; import com.yankardev.sitrac.flota.infrastructure.adapter.in.rest.dto.*;
import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/tractos") @RequiredArgsConstructor
public class TractoController {
    private final TractoUseCase useCase;
    @PostMapping public ResponseEntity<TractoResponse> crear(@Valid @RequestBody TractoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(useCase.crear(toDomain(r,true))));}
    @GetMapping public List<TractoResponse> listar(){return useCase.listar().stream().map(this::toResponse).toList();}
    @GetMapping("/{id}") public TractoResponse obtener(@PathVariable Long id){return toResponse(useCase.obtenerPorId(id));}
    @PutMapping("/{id}") public TractoResponse actualizar(@PathVariable Long id,@Valid @RequestBody TractoRequest r){return toResponse(useCase.actualizar(id,toDomain(r,false)));}
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){useCase.eliminar(id);return ResponseEntity.noContent().build();}
    private Tracto toDomain(TractoRequest r,boolean nuevo){return Tracto.builder().placa(r.placa().toUpperCase()).marca(r.marca()).modelo(r.modelo()).anio(r.anio()).capacidadToneladas(r.capacidadToneladas()).estado(r.estado()).activo(nuevo||r.activo()==null||r.activo()).build();}
    private TractoResponse toResponse(Tracto t){return new TractoResponse(t.getId(),t.getPlaca(),t.getMarca(),t.getModelo(),t.getAnio(),t.getCapacidadToneladas(),t.getEstado(),t.isActivo());}
}