package com.yankardev.sitrac.somma.infrastructure.adapter.in.rest;
import com.yankardev.sitrac.somma.domain.model.RegistroSomma;import com.yankardev.sitrac.somma.domain.port.in.RegistroSommaUseCase;import com.yankardev.sitrac.somma.infrastructure.adapter.in.rest.dto.*;
import jakarta.validation.Valid;import lombok.RequiredArgsConstructor;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/somma") @RequiredArgsConstructor
public class RegistroSommaController{
 private final RegistroSommaUseCase useCase;
 @PostMapping public ResponseEntity<RegistroSommaResponse> crear(@Valid @RequestBody RegistroSommaRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(resp(useCase.crear(dom(r))));}
 @GetMapping public List<RegistroSommaResponse> listar(){return useCase.listar().stream().map(this::resp).toList();}
 @GetMapping("/{id}") public RegistroSommaResponse obtener(@PathVariable Long id){return resp(useCase.obtenerPorId(id));}
 @PutMapping("/{id}") public RegistroSommaResponse actualizar(@PathVariable Long id,@Valid @RequestBody RegistroSommaRequest r){return resp(useCase.actualizar(id,dom(r)));}
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id){useCase.eliminar(id);return ResponseEntity.noContent().build();}
 private RegistroSomma dom(RegistroSommaRequest r){return RegistroSomma.builder().tipo(r.tipo()).conductorId(r.conductorId()).fecha(r.fecha()).titulo(r.titulo()).descripcion(r.descripcion()).lugar(r.lugar()).estado(r.estado()).build();}
 private RegistroSommaResponse resp(RegistroSomma r){return new RegistroSommaResponse(r.getId(),r.getTipo(),r.getConductorId(),r.getFecha(),r.getTitulo(),r.getDescripcion(),r.getLugar(),r.getEstado());}
}