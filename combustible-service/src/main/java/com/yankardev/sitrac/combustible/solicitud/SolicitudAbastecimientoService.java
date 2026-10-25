package com.yankardev.sitrac.combustible.solicitud;

import com.yankardev.sitrac.combustible.application.exception.ReglaNegocioException;
import com.yankardev.sitrac.combustible.domain.port.out.ProgramacionConsultaPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SolicitudAbastecimientoService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudAbastecimientoService.class);
    private static final String ROUTING_KEY_CONFIRMADA = "combustible.abastecimiento.confirmado";

    private final SolicitudAbastecimientoRepository repository;
    private final ProgramacionConsultaPort programacionConsulta;
    private final RabbitTemplate rabbitTemplate;

    public SolicitudAbastecimientoService(
            SolicitudAbastecimientoRepository repository,
            ProgramacionConsultaPort programacionConsulta,
            RabbitTemplate rabbitTemplate
    ) {
        this.repository = repository;
        this.programacionConsulta = programacionConsulta;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<SolicitudAbastecimiento> listar() {
        return repository.findAll().stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .toList();
    }

    @Transactional
    public SolicitudAbastecimiento crearDesdeProgramacion(Long programacionId) {
        return repository.findByProgramacionId(programacionId)
                .orElseGet(() -> crearNueva(programacionId));
    }

    @Transactional
    public SolicitudAbastecimiento confirmar(
            Long id,
            String puntoAbastecimiento,
            BigDecimal cantidadGalones,
            String observacion
    ) {
        SolicitudAbastecimiento solicitud = repository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Solicitud de abastecimiento no encontrada: " + id));

        if (solicitud.getEstado() == EstadoSolicitudAbastecimiento.CONFIRMADA) {
            return solicitud;
        }

        if (puntoAbastecimiento == null || puntoAbastecimiento.isBlank()) {
            throw new ReglaNegocioException("El punto de abastecimiento es obligatorio");
        }

        if (cantidadGalones == null || cantidadGalones.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("La cantidad de galones debe ser mayor que cero");
        }

        solicitud.setPuntoAbastecimiento(puntoAbastecimiento.trim());
        solicitud.setCantidadGalones(cantidadGalones);
        solicitud.setObservacion(limpiar(observacion));
        solicitud.setEstado(EstadoSolicitudAbastecimiento.CONFIRMADA);
        solicitud.setFechaConfirmacion(LocalDateTime.now());

        SolicitudAbastecimiento confirmada = repository.save(solicitud);
        publicarConfirmacion(confirmada);
        return confirmada;
    }

    /**
     * Reenvía únicamente el evento de notificación de una solicitud que ya fue
     * confirmada. Se utiliza como recuperación de evidencia para solicitudes
     * históricas confirmadas antes de que notification-service estuviera disponible.
     */
    @Transactional(readOnly = true)
    public SolicitudAbastecimiento notificarConfirmada(Long id) {
        SolicitudAbastecimiento solicitud = repository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Solicitud de abastecimiento no encontrada: " + id));

        if (solicitud.getEstado() != EstadoSolicitudAbastecimiento.CONFIRMADA) {
            throw new ReglaNegocioException("Solo se puede notificar una solicitud confirmada");
        }

        if (solicitud.getPuntoAbastecimiento() == null || solicitud.getCantidadGalones() == null
                || solicitud.getFechaConfirmacion() == null) {
            throw new ReglaNegocioException("La solicitud confirmada no tiene datos completos de abastecimiento");
        }

        publicarConfirmacion(solicitud);
        return solicitud;
    }

    private SolicitudAbastecimiento crearNueva(Long programacionId) {
        ProgramacionConsultaPort.ProgramacionOperacion programacion = programacionConsulta.buscarProgramacion(programacionId)
                .orElseThrow(() -> new ReglaNegocioException(
                        "No se pudo generar la solicitud: la programación " + programacionId + " no existe"
                ));

        if (!"PROGRAMADA".equals(programacion.estado())) {
            throw new ReglaNegocioException(
                    "No se pudo generar la solicitud: la programación debe estar en estado PROGRAMADA"
            );
        }

        SolicitudAbastecimiento solicitud = SolicitudAbastecimiento.builder()
                .programacionId(programacion.id())
                .conductorId(programacion.conductorId())
                .tractoId(programacion.tractoId())
                .fechaGeneracion(LocalDateTime.now())
                .estado(EstadoSolicitudAbastecimiento.PENDIENTE)
                .build();

        SolicitudAbastecimiento guardada = repository.save(solicitud);
        log.info("Solicitud de abastecimiento {} creada para programación {}", guardada.getId(), programacionId);
        return guardada;
    }

    private void publicarConfirmacion(SolicitudAbastecimiento solicitud) {
        Map<String, Object> evento = new LinkedHashMap<>();
        evento.put("tipoEvento", "ABASTECIMIENTO_CONFIRMADO");
        evento.put("solicitudId", solicitud.getId());
        evento.put("programacionId", solicitud.getProgramacionId());
        evento.put("conductorId", solicitud.getConductorId());
        evento.put("tractoId", solicitud.getTractoId());
        evento.put("puntoAbastecimiento", solicitud.getPuntoAbastecimiento());
        evento.put("cantidadGalones", solicitud.getCantidadGalones());
        evento.put("fechaConfirmacion", solicitud.getFechaConfirmacion().toString());
        evento.put("observacion", solicitud.getObservacion() == null ? "" : solicitud.getObservacion());

        try {
            rabbitTemplate.convertAndSend(RabbitCombustibleConfig.EXCHANGE, ROUTING_KEY_CONFIRMADA, evento);
            log.info("Evento ABASTECIMIENTO_CONFIRMADO publicado para solicitud {}", solicitud.getId());
        } catch (AmqpException ex) {
            log.warn("La solicitud {} fue confirmada, pero no se pudo publicar la notificación: {}",
                    solicitud.getId(), ex.getMessage());
            throw new ReglaNegocioException("No se pudo publicar la notificación de abastecimiento");
        }
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
