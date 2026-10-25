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

    private static final String PASSWORD_DEMO = "Sitrac2026*";

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Override
    public void run(ApplicationArguments args) {
        crearSiNoExiste("admin.sitrac", PASSWORD_DEMO, "Administrador SITRAC", Rol.ADMIN);
        crearSiNoExiste("operador.sitrac", PASSWORD_DEMO, "Operador de Transporte", Rol.OPERADOR);
        crearSiNoExiste("combustible.sitrac", PASSWORD_DEMO, "Responsable de Combustible", Rol.COMBUSTIBLE);
        crearSiNoExiste("supervisor.sitrac", PASSWORD_DEMO, "Supervisor de Operaciones", Rol.SUPERVISOR);
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
