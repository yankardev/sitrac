package com.yankardev.sitrac.notificacion.registro;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones_evidencia")
public class RegistroNotificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_evento", nullable = false, length = 60)
    private String tipoEvento;

    @Column(length = 100)
    private String referencia;

    @Column(length = 180)
    private String destinatario;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoNotificacion estado;

    @Column(length = 500)
    private String detalle;

    @Column(name = "tiene_adjunto", nullable = false)
    private boolean tieneAdjunto;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    protected RegistroNotificacion() {
    }

    public RegistroNotificacion(
            String tipoEvento,
            String referencia,
            String destinatario,
            String asunto,
            EstadoNotificacion estado,
            String detalle,
            boolean tieneAdjunto,
            LocalDateTime fechaRegistro
    ) {
        this.tipoEvento = tipoEvento;
        this.referencia = referencia;
        this.destinatario = destinatario;
        this.asunto = asunto;
        this.estado = estado;
        this.detalle = detalle;
        this.tieneAdjunto = tieneAdjunto;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getId() {
        return id;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public String getDetalle() {
        return detalle;
    }

    public boolean isTieneAdjunto() {
        return tieneAdjunto;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
