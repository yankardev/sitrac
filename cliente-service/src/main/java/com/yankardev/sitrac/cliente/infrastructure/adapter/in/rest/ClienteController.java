package com.yankardev.sitrac.cliente.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.cliente.domain.model.Cliente;
import com.yankardev.sitrac.cliente.domain.port.in.ClienteUseCase;
import com.yankardev.sitrac.cliente.infrastructure.adapter.in.rest.dto.ActualizarClienteRequest;
import com.yankardev.sitrac.cliente.infrastructure.adapter.in.rest.dto.ClienteResponse;
import com.yankardev.sitrac.cliente.infrastructure.adapter.in.rest.dto.CrearClienteRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody CrearClienteRequest request) {
        Cliente creado = clienteUseCase.crear(Cliente.builder()
                .tipoDocumento(request.tipoDocumento())
                .numeroDocumento(request.numeroDocumento())
                .nombreRazonSocial(request.nombreRazonSocial())
                .telefono(request.telefono())
                .email(request.email())
                .direccion(request.direccion())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creado));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        List<ClienteResponse> clientes = clienteUseCase.listar()
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(clienteUseCase.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarClienteRequest request) {

        Cliente actualizado = clienteUseCase.actualizar(id, Cliente.builder()
                .tipoDocumento(request.tipoDocumento())
                .numeroDocumento(request.numeroDocumento())
                .nombreRazonSocial(request.nombreRazonSocial())
                .telefono(request.telefono())
                .email(request.email())
                .direccion(request.direccion())
                .activo(request.activo())
                .build());

        return ResponseEntity.ok(toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getNombreRazonSocial(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.getDireccion(),
                cliente.isActivo()
        );
    }
}
