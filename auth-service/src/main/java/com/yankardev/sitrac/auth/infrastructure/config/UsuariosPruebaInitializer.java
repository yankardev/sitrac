package com.yankardev.sitrac.auth.infrastructure.config;

import com.yankardev.sitrac.auth.domain.model.Rol;
import com.yankardev.sitrac.auth.domain.port.in.RegistrarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuariosPruebaInitializer implements ApplicationRunner {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Override
    public void run(ApplicationArguments args) {
        crearSiNoExiste("admin.sitrac", "Admin123*", "Administrador SITRAC", Rol.ADMIN);
        crearSiNoExiste("operador.sitrac", "Operador123*", "Operador de Transporte", Rol.OPERADOR);
        crearSiNoExiste("somma.sitrac", "Somma123*", "Responsable SOMMA", Rol.SOMMA);
        crearSiNoExiste("mantenimiento.sitrac", "Mantenimiento123*", "Responsable de Mantenimiento", Rol.MANTENIMIENTO);
        crearSiNoExiste("supervisor.sitrac", "Supervisor123*", "Supervisor de Operaciones", Rol.SUPERVISOR);
    }

    private void crearSiNoExiste(
            String username,
            String password,
            String nombreCompleto,
            Rol rol
    ) {
        try {
            registrarUsuarioUseCase.registrar(username, password, nombreCompleto, rol);
        } catch (IllegalArgumentException ignored) {
            // Si el usuario ya existe, se conserva sin modificar su contraseña.
        }
    }
}
