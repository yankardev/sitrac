package com.yankardev.sitrac.notificacion.messaging;

import com.yankardev.sitrac.notificacion.client.ClienteClient;
import com.yankardev.sitrac.notificacion.client.ConductorClient;
import com.yankardev.sitrac.notificacion.client.PedidoClient;
import com.yankardev.sitrac.notificacion.client.ProgramacionClient;
import com.yankardev.sitrac.notificacion.config.RabbitMqConfig;
import com.yankardev.sitrac.notificacion.service.ConstanciaPdfService;
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
    private final ConductorClient conductorClient;
    private final CorreoService correoService;
    private final ConstanciaPdfService constanciaPdfService;

    public NotificacionListener(
            ProgramacionClient programacionClient,
            PedidoClient pedidoClient,
            ClienteClient clienteClient,
            ConductorClient conductorClient,
            CorreoService correoService,
            ConstanciaPdfService constanciaPdfService
    ) {
        this.programacionClient = programacionClient;
        this.pedidoClient = pedidoClient;
        this.clienteClient = clienteClient;
        this.conductorClient = conductorClient;
        this.correoService = correoService;
        this.constanciaPdfService = constanciaPdfService;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void procesar(Map<String, Object> evento) {
        String tipoEvento = String.valueOf(evento.get("tipoEvento"));

        try {
            switch (tipoEvento) {
                case "ABASTECIMIENTO_CONFIRMADO" -> notificarAbastecimiento(evento);
                case "VIAJE_FINALIZADO" -> notificarViajeFinalizado(evento);
                default -> log.warn("Evento SITRAC no reconocido por notification-service: {}", tipoEvento);
            }
        } catch (Exception ex) {
            log.error("No se pudo procesar el evento {}: {}", tipoEvento, ex.getMessage(), ex);
        }
    }

    private void notificarAbastecimiento(Map<String, Object> evento) {
        Long solicitudId = longValue(evento.get("solicitudId"));
        Long programacionId = longValue(evento.get("programacionId"));
        Long conductorId = longValue(evento.get("conductorId"));
        Long tractoId = longValue(evento.get("tractoId"));
        String punto = textValue(evento.get("puntoAbastecimiento"));
        String cantidad = textValue(evento.get("cantidadGalones"));
        String fecha = textValue(evento.get("fechaConfirmacion"));
        String observacion = textValue(evento.get("observacion"));

        ConductorClient.ConductorResponse conductor = conductorClient.obtener(conductorId);
        String nombreConductor = nombreConductor(conductor);

        String asunto = "SITRAC - Abastecimiento asignado";
        String cuerpo = "Hola " + nombreConductor + ",\n\n"
                + "Se confirmó el abastecimiento correspondiente a su servicio.\n"
                + "Solicitud: " + solicitudId + "\n"
                + "Programación: " + programacionId + "\n"
                + "Tracto: " + tractoId + "\n"
                + "Punto de abastecimiento: " + punto + "\n"
                + "Cantidad autorizada: " + cantidad + " galones\n"
                + "Fecha de confirmación: " + fecha + "\n"
                + (observacion.isBlank() ? "" : "Observación: " + observacion + "\n")
                + "\nSITRAC - Sistema Integral de Transporte de Carga";

        correoService.enviar(conductor.email(), asunto, cuerpo);
    }

    private void notificarViajeFinalizado(Map<String, Object> evento) {
        Long viajeId = longValue(evento.get("viajeId"));
        Long programacionId = longValue(evento.get("programacionId"));
        String fechaFinalizacion = textValue(evento.get("fechaFinalizacion"));

        ProgramacionClient.ProgramacionResponse programacion = programacionClient.obtener(programacionId);
        PedidoClient.PedidoResponse pedido = pedidoClient.obtener(programacion.pedidoId());
        ClienteClient.ClienteResponse cliente = clienteClient.obtener(pedido.clienteId());

        String asunto = "SITRAC - Servicio de transporte finalizado";
        String cuerpo = "Hola " + nombreCliente(cliente) + ",\n\n"
                + "El servicio de transporte asociado a su pedido ha finalizado.\n"
                + "Pedido: " + pedido.id() + "\n"
                + "Programación: " + programacionId + "\n"
                + "Viaje: " + viajeId + "\n"
                + "Fecha de finalización: " + fechaFinalizacion + "\n\n"
                + "Se adjunta la constancia digital del servicio.\n\n"
                + "SITRAC - Sistema Integral de Transporte de Carga";

        byte[] constancia = constanciaPdfService.generar(
                viajeId,
                programacionId,
                pedido.id(),
                nombreCliente(cliente),
                fechaFinalizacion
        );

        correoService.enviarConAdjunto(
                cliente.email(),
                asunto,
                cuerpo,
                constancia,
                "constancia-servicio-viaje-" + viajeId + ".pdf"
        );
    }

    private String nombreCliente(ClienteClient.ClienteResponse cliente) {
        return cliente.nombreRazonSocial() == null || cliente.nombreRazonSocial().isBlank()
                ? "cliente"
                : cliente.nombreRazonSocial();
    }

    private String nombreConductor(ConductorClient.ConductorResponse conductor) {
        String nombres = conductor.nombres() == null ? "" : conductor.nombres().trim();
        String apellidos = conductor.apellidos() == null ? "" : conductor.apellidos().trim();
        String completo = (nombres + " " + apellidos).trim();
        return completo.isBlank() ? "conductor" : completo;
    }

    private String textValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private Long longValue(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }
}
