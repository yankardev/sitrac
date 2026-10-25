package com.yankardev.sitrac.viaje.infrastructure.messaging;

import com.yankardev.sitrac.viaje.domain.port.out.NotificacionEventPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RabbitNotificacionEventAdapter implements NotificacionEventPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitNotificacionEventAdapter.class);
    private static final String ROUTING_KEY = "viaje.finalizado";

    private final RabbitTemplate rabbitTemplate;

    public RabbitNotificacionEventAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void viajeFinalizado(Long viajeId, Long programacionId, LocalDateTime fechaFinalizacion) {
        Map<String, Object> evento = new LinkedHashMap<>();
        evento.put("tipoEvento", "VIAJE_FINALIZADO");
        evento.put("viajeId", viajeId);
        evento.put("programacionId", programacionId);
        evento.put("fechaFinalizacion", fechaFinalizacion == null ? "" : fechaFinalizacion.toString());

        try {
            rabbitTemplate.convertAndSend(RabbitEventConfig.EXCHANGE, ROUTING_KEY, evento);
            log.info("Evento VIAJE_FINALIZADO publicado para viaje {}", viajeId);
        } catch (AmqpException ex) {
            log.warn("El viaje {} fue finalizado, pero no se pudo publicar la notificación: {}",
                    viajeId, ex.getMessage());
        }
    }
}
