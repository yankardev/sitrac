package com.yankardev.sitrac.notificacion.messaging;

import com.yankardev.sitrac.notificacion.client.ClienteClient;
import com.yankardev.sitrac.notificacion.client.PedidoClient;
import com.yankardev.sitrac.notificacion.client.ProgramacionClient;
import com.yankardev.sitrac.notificacion.config.RabbitMqConfig;
import com.yankardev.sitrac.notificacion.service.CorreoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificacionListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacionListener.class);

    private final ProgramacionClient programacionClient;
    private final PedidoClient pedidoClient;
    private final ClienteClient clienteClient;
    private final CorreoService correoService;

    public NotificacionListener(
            ProgramacionClient programacionClient,
            PedidoClient pedidoClient,
            ClienteClient clienteClient,
            CorreoService correoService
    ) {
        this.programacionClient = programacionClient;
        this.pedidoClient = pedidoClient;
        this.clienteClient = clienteClient;
        this.correoService = correoService;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void procesar(Map<String, Object> evento) {
        String tipoEvento = String.valueOf(evento.get("tipoEvento"));

        try {
            switch (tipoEvento) {
                case "PROGRAMACION_CREADA" -> notificarProgramacion(evento);
                case "VIAJE_FINALIZADO" -> notificarViajeFinalizado(evento);
                default -> log.warn("Evento SITRAC no reconocido: {}", tipoEvento);
            }
        } catch (Exception ex) {
            log.error("No se pudo procesar el evento {}: {}", tipoEvento, ex.getMessage(), ex);
        }
    }

    private void notificarProgramacion(Map<String, Object> evento) {
        Long pedidoId = longValue(evento.get("pedidoId"));
        Long programacionId = longValue(evento.get("programacionId"));
        String fechaProgramada = String.valueOf(evento.get("fechaProgramada"));

        ClienteClient.ClienteResponse cliente = clienteDePedido(pedidoId);

        String asunto = "SITRAC - Servicio de transporte programado";
        String cuerpo = "Hola " + nombre(cliente) + ",\n\n"
                + "Su servicio de transporte fue programado correctamente.\n"
                + "Pedido: " + pedidoId + "\n"
                + "Programación: " + programacionId + "\n"
                + "Fecha programada: " + fechaProgramada + "\n\n"
                + "SITRAC - Sistema Integral de Transporte de Carga";

        correoService.enviar(cliente.email(), asunto, cuerpo);
    }

    private void notificarViajeFinalizado(Map<String, Object> evento) {
        Long viajeId = longValue(evento.get("viajeId"));
        Long programacionId = longValue(evento.get("programacionId"));
        String fechaFinalizacion = String.valueOf(evento.get("fechaFinalizacion"));

        ProgramacionClient.ProgramacionResponse programacion = programacionClient.obtener(programacionId);
        ClienteClient.ClienteResponse cliente = clienteDePedido(programacion.pedidoId());

        String asunto = "SITRAC - Viaje finalizado";
        String cuerpo = "Hola " + nombre(cliente) + ",\n\n"
                + "El viaje asociado a su servicio de transporte ha finalizado.\n"
                + "Viaje: " + viajeId + "\n"
                + "Programación: " + programacionId + "\n"
                + "Fecha de finalización: " + fechaFinalizacion + "\n\n"
                + "SITRAC - Sistema Integral de Transporte de Carga";

        correoService.enviar(cliente.email(), asunto, cuerpo);
    }

    private ClienteClient.ClienteResponse clienteDePedido(Long pedidoId) {
        PedidoClient.PedidoResponse pedido = pedidoClient.obtener(pedidoId);
        return clienteClient.obtener(pedido.clienteId());
    }

    private String nombre(ClienteClient.ClienteResponse cliente) {
        return cliente.nombreRazonSocial() == null || cliente.nombreRazonSocial().isBlank()
                ? "cliente"
                : cliente.nombreRazonSocial();
    }

    private Long longValue(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }
}
