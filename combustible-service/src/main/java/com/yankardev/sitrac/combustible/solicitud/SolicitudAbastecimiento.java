package com.yankardev.sitrac.combustible.solicitud;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "solicitudes_abastecimiento",
        uniqueConstraints = @UniqueConstraint(name = "uk_solicitud_programacion", columnNames = "programacion_id")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAbastecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "programacion_id", nullable = false)
    private Long programacionId;

    @Column(name = "conductor_id", nullable = false)
    private Long conductorId;

    @Column(name = "tracto_id", nullable = false)
    private Long tractoId;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitudAbastecimiento estado;

    @Column(name = "punto_abastecimiento", length = 180)
    private String puntoAbastecimiento;

    @Column(name = "cantidad_galones", precision = 12, scale = 3)
    private BigDecimal cantidadGalones;

    @Column(length = 500)
    private String observacion;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;
}
