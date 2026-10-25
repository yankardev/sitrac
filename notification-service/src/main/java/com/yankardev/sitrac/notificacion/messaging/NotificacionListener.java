package com.yankardev.sitrac.notificacion.messaging;

import com.yankardev.sitrac.notificacion.client.ClienteClient;
import com.yankardev.sitrac.notificacion.client.ConductorClient;
import com.yankardev.sitrac.notificacion.client.PedidoClient;
import com.yankardev.sitrac.notificacion.client.ProgramacionClient;
import com.yankardev.sitrac.notificacion.config.RabbitMqConfig;
import com.yankardev.sitrac.notificacion.registro.RegistroNotificacionService;
import com.yankardev.sitrac.notificacion.service.ConstanciaPdfService;
import com.yankardev.sitrac.notificacion.service.CorreoService;
import com.yankardev.sitrac.notificacion.service.ResultadoCorreo;
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
    private final RegistroNotificacionService registroNotificacionService;

    public NotificacionListener(
            ProgramacionClient programacionClient,
            PedidoClient pedidoClient,
            ClienteClient clienteClient,
            ConductorClient conductorClient,
            CorreoService correoService,
            ConstanciaPdfService constanciaPdfService,
            RegistroNotificacionService registroNotificacionService
    ) {
        this.programacionClient = programacionClient;
        this.pedidoClient = pedidoClient;
        this.clienteClient = clienteClient;
        this.conductorClient = conductorClient;
        this.correoService = correoService;
        this.constanciaPdfService = constanciaPdfService;
        this.registroNotificacionService = registroNotificacionService;
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
            registrarErrorSeguro(
                    tipoEvento,
                    referenciaEvento(evento),
                    "",
                    "Error al procesar notificación SITRAC",
                    ex.getMessage(),
                    "VIAJE_FINALIZADO".equals(tipoEvento)
            );
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

        ResultadoCorreo resultado = correoService.enviar(conductor.email(), asunto, cuerpo);
        registroNotificacionService.registrar(
                "ABASTECIMIENTO_CONFIRMADO",
                "SOL-" + solicitudId + " / PROG-" + programacionId,
                conductor.email(),
                asunto,
                resultado,
                "Abastecimiento confirmado para " + nombreConductor
                        + ". Punto: " + punto + ". Cantidad: " + cantidad + " galones.",
                false
        );
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

        ResultadoCorreo resultado = correoService.enviarConAdjunto(
                cliente.email(),
                asunto,
                cuerpo,
                constancia,
                "constancia-servicio-viaje-" + viajeId + ".pdf"
        );

        registroNotificacionService.registrar(
                "VIAJE_FINALIZADO",
                "VIA-" + viajeId + " / PED-" + pedido.id(),
                cliente.email(),
                asunto,
                resultado,
                "Servicio finalizado para " + nombreCliente(cliente)
                        + ". Se generó la constancia PDF del viaje " + viajeId + ".",
                true
        );
    }

    private void registrarErrorSeguro(
            String tipoEvento,
            String referencia,
            String destinatario,
            String asunto,
            String detalle,
            boolean tieneAdjunto
    ) {
        try {
            registroNotificacionService.registrarError(
                    tipoEvento,
                    referencia,
                    destinatario,
                    asunto,
                    detalle,
                    tieneAdjunto
            );
        } catch (Exception registroEx) {
            log.error("No se pudo guardar la evidencia del error de notificación: {}", registroEx.getMessage());
        }
    }

    private String referenciaEvento(Map<String, Object> evento) {
        if (evento.get("viajeId") != null) {
            return "VIA-" + evento.get("viajeId");
        }
        if (evento.get("solicitudId") != null) {
            return "SOL-" + evento.get("solicitudId");
        }
        if (evento.get("programacionId") != null) {
            return "PROG-" + evento.get("programacionId");
        }
        return "";
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
