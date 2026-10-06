package com.yankardev.sitrac.auth.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.auth.domain.model.Usuario;
import com.yankardev.sitrac.auth.domain.port.in.GestionUsuarioUseCase;
import com.yankardev.sitrac.auth.domain.port.in.RegistrarUsuarioUseCase;
import com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto.ActualizarUsuarioRequest;
import com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto.CambiarEstadoUsuarioRequest;
import com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto.RegistroUsuarioRequest;
import com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto.RestablecerPasswordRequest;
import com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/usuarios")
@RequiredArgsConstructor
public class UsuarioAdminController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final GestionUsuarioUseCase gestionUsuarioUseCase;

    @GetMapping
    public List<UsuarioResponse> listar() {
        return gestionUsuarioUseCase.listar().stream().map(this::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody RegistroUsuarioRequest request) {
        Usuario usuario = registrarUsuarioUseCase.registrar(
                request.username(),
                request.password(),
                request.nombreCompleto(),
                request.rol()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(usuario));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequest request
    ) {
        return toResponse(gestionUsuarioUseCase.actualizar(
                id,
                request.username(),
                request.nombreCompleto(),
                request.rol()
        ));
    }

    @PutMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoUsuarioRequest request
    ) {
        return toResponse(gestionUsuarioUseCase.cambiarEstado(id, request.activo()));
    }

    @PutMapping("/{id}/password")
    public UsuarioResponse restablecerPassword(
            @PathVariable Long id,
            @Valid @RequestBody RestablecerPasswordRequest request
    ) {
        return toResponse(gestionUsuarioUseCase.restablecerPassword(id, request.password()));
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }
}
