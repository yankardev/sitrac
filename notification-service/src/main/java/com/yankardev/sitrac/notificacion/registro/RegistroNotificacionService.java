package com.yankardev.sitrac.notificacion.registro;

import com.yankardev.sitrac.notificacion.service.ResultadoCorreo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistroNotificacionService {

    private final RegistroNotificacionRepository repository;

    public RegistroNotificacionService(RegistroNotificacionRepository repository) {
        this.repository = repository;
    }

    public RegistroNotificacion registrar(
            String tipoEvento,
            String referencia,
            String destinatario,
            String asunto,
            ResultadoCorreo resultado,
            String detalle,
            boolean tieneAdjunto
    ) {
        EstadoNotificacion estado = switch (resultado) {
            case ENVIADA -> EstadoNotificacion.ENVIADA;
            case SIMULADA -> EstadoNotificacion.SIMULADA;
            case OMITIDA -> EstadoNotificacion.OMITIDA;
        };

        return guardar(tipoEvento, referencia, destinatario, asunto, estado, detalle, tieneAdjunto);
    }

    public RegistroNotificacion registrarError(
            String tipoEvento,
            String referencia,
            String destinatario,
            String asunto,
            String detalle,
            boolean tieneAdjunto
    ) {
        return guardar(
                tipoEvento,
                referencia,
                destinatario,
                asunto,
                EstadoNotificacion.ERROR,
                detalle,
                tieneAdjunto
        );
    }

    public List<RegistroNotificacion> listar() {
        return repository.findAllByOrderByFechaRegistroDesc();
    }

    private RegistroNotificacion guardar(
            String tipoEvento,
            String referencia,
            String destinatario,
            String asunto,
            EstadoNotificacion estado,
            String detalle,
            boolean tieneAdjunto
    ) {
        RegistroNotificacion registro = new RegistroNotificacion(
                valor(tipoEvento, "EVENTO_SITRAC"),
                valor(referencia, ""),
                valor(destinatario, ""),
                valor(asunto, "Notificación SITRAC"),
                estado,
                limitar(detalle, 500),
                tieneAdjunto,
                LocalDateTime.now()
        );
        return repository.save(registro);
    }

    private String valor(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String limitar(String value, int max) {
        if (value == null) {
            return "";
        }
        String limpio = value.trim();
        return limpio.length() <= max ? limpio : limpio.substring(0, max);
    }
}
