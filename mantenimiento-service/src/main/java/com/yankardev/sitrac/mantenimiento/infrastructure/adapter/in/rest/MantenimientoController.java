package com.yankardev.sitrac.mantenimiento.infrastructure.adapter.in.rest;
import com.yankardev.sitrac.mantenimiento.domain.model.Mantenimiento;import com.yankardev.sitrac.mantenimiento.domain.port.in.MantenimientoUseCase;import com.yankardev.sitrac.mantenimiento.infrastructure.adapter.in.rest.dto.*;
import jakarta.validation.Valid;import lombok.RequiredArgsConstructor;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/mantenimientos") @RequiredArgsConstructor
public class MantenimientoController{
 private final MantenimientoUseCase useCase;
 @PostMapping public ResponseEntity<MantenimientoResponse> crear(@Valid @RequestBody MantenimientoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(resp(useCase.crear(dom(r))));}
 @GetMapping public List<MantenimientoResponse> listar(){return useCase.listar().stream().map(this::resp).toList();}
 @GetMapping("/{id}") public MantenimientoResponse obtener(@PathVariable Long id){return resp(useCase.obtenerPorId(id));}
 @PutMapping("/{id}") public MantenimientoResponse actualizar(@PathVariable Long id,@Valid @RequestBody MantenimientoRequest r){return resp(useCase.actualizar(id,dom(r)));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){useCase.eliminar(id);return ResponseEntity.noContent().build();}
 private Mantenimiento dom(MantenimientoRequest r){return Mantenimiento.builder().tipoUnidad(r.tipoUnidad()).unidadId(r.unidadId()).tipoMantenimiento(r.tipoMantenimiento()).fechaInicio(r.fechaInicio()).fechaFin(r.fechaFin()).descripcion(r.descripcion()).costo(r.costo()).estado(r.estado()).build();}
 private MantenimientoResponse resp(Mantenimiento m){return new MantenimientoResponse(m.getId(),m.getTipoUnidad(),m.getUnidadId(),m.getTipoMantenimiento(),m.getFechaInicio(),m.getFechaFin(),m.getDescripcion(),m.getCosto(),m.getEstado());}
}