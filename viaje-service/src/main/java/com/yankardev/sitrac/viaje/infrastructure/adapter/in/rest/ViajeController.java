package com.yankardev.sitrac.viaje.infrastructure.adapter.in.rest;
import com.yankardev.sitrac.viaje.domain.model.Viaje;import com.yankardev.sitrac.viaje.domain.port.in.ViajeUseCase;import com.yankardev.sitrac.viaje.infrastructure.adapter.in.rest.dto.*;
import jakarta.validation.Valid;import lombok.RequiredArgsConstructor;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/viajes") @RequiredArgsConstructor
public class ViajeController{
 private final ViajeUseCase useCase;
 @PostMapping public ResponseEntity<ViajeResponse> crear(@Valid @RequestBody ViajeRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(resp(useCase.crear(dom(r))));}
 @GetMapping public List<ViajeResponse> listar(){return useCase.listar().stream().map(this::resp).toList();}
 @GetMapping("/{id}") public ViajeResponse obtener(@PathVariable Long id){return resp(useCase.obtenerPorId(id));}
 @PutMapping("/{id}") public ViajeResponse actualizar(@PathVariable Long id,@Valid @RequestBody ViajeRequest r){return resp(useCase.actualizar(id,dom(r)));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){useCase.eliminar(id);return ResponseEntity.noContent().build();}
 private Viaje dom(ViajeRequest r){return Viaje.builder().programacionId(r.programacionId()).fechaInicio(r.fechaInicio()).fechaFin(r.fechaFin()).kilometrajeInicial(r.kilometrajeInicial()).kilometrajeFinal(r.kilometrajeFinal()).observacion(r.observacion()).estado(r.estado()).build();}
 private ViajeResponse resp(Viaje v){return new ViajeResponse(v.getId(),v.getProgramacionId(),v.getFechaInicio(),v.getFechaFin(),v.getKilometrajeInicial(),v.getKilometrajeFinal(),v.getObservacion(),v.getEstado());}
}