package com.yankardev.sitrac.notificacion.registro;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class RegistroNotificacionController {

    private final RegistroNotificacionService service;

    public RegistroNotificacionController(RegistroNotificacionService service) {
        this.service = service;
    }

    @GetMapping
    public List<RegistroNotificacion> listar() {
        return service.listar();
    }
}
