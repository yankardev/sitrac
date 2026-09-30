package com.yankardev.sitrac.auth.infrastructure.adapter.in.rest;

import com.yankardev.sitrac.auth.domain.model.Usuario;
import com.yankardev.sitrac.auth.domain.port.in.LoginUseCase;
import com.yankardev.sitrac.auth.domain.port.in.RegistrarUsuarioUseCase;
import com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto.LoginResponse;
import com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto.RegistroUsuarioRequest;
import com.yankardev.sitrac.auth.infrastructure.adapter.in.rest.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = loginUseCase.login(request.username(), request.password());

        return ResponseEntity.ok(new LoginResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol()
        ));
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        Usuario usuario = registrarUsuarioUseCase.registrar(
                request.username(),
                request.password(),
                request.nombreCompleto(),
                request.rol()
        );

        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getNombreCompleto(),
                usuario.getRol(),
                usuario.isActivo()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
