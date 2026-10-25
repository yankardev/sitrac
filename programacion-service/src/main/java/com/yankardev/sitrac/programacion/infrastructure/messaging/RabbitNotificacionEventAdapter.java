package com.yankardev.sitrac.programacion.infrastructure.messaging;

import com.yankardev.sitrac.programacion.domain.port.out.NotificacionEventPort;
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
    private static final String ROUTING_KEY = "programacion.creada";

    private final RabbitTemplate rabbitTemplate;

    public RabbitNotificacionEventAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void programacionCreada(Long programacionId, Long pedidoId, LocalDateTime fechaProgramada) {
        Map<String, Object> evento = new LinkedHashMap<>();
        evento.put("tipoEvento", "PROGRAMACION_CREADA");
        evento.put("programacionId", programacionId);
        evento.put("pedidoId", pedidoId);
        evento.put("fechaProgramada", fechaProgramada == null ? "" : fechaProgramada.toString());

        try {
            rabbitTemplate.convertAndSend(RabbitEventConfig.EXCHANGE, ROUTING_KEY, evento);
            log.info("Evento PROGRAMACION_CREADA publicado para programación {}", programacionId);
        } catch (AmqpException ex) {
            log.warn("La programación {} fue creada, pero no se pudo publicar la notificación: {}",
                    programacionId, ex.getMessage());
        }
    }
}
