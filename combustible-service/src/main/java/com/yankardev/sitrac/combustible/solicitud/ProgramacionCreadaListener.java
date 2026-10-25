package com.yankardev.sitrac.combustible.solicitud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProgramacionCreadaListener {

    private static final Logger log = LoggerFactory.getLogger(ProgramacionCreadaListener.class);

    private final SolicitudAbastecimientoService service;

    public ProgramacionCreadaListener(SolicitudAbastecimientoService service) {
        this.service = service;
    }

    @RabbitListener(queues = RabbitCombustibleConfig.QUEUE)
    public void procesar(Map<String, Object> evento) {
        String tipoEvento = String.valueOf(evento.get("tipoEvento"));
        if (!"PROGRAMACION_CREADA".equals(tipoEvento)) {
            log.debug("Evento ignorado por combustible-service: {}", tipoEvento);
            return;
        }

        Long programacionId = longValue(evento.get("programacionId"));
        service.crearDesdeProgramacion(programacionId);
    }

    private Long longValue(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }
}
