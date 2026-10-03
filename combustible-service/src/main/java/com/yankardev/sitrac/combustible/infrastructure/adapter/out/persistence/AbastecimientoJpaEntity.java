package com.yankardev.sitrac.combustible.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.combustible.domain.model.TipoAbastecimiento;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "abastecimientos_combustible")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbastecimientoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "programacion_id", nullable = false)
    private Long programacionId;

    @Column(name = "viaje_id")
    private Long viajeId;

    @Column(name = "conductor_id", nullable = false)
    private Long conductorId;

    @Column(name = "tracto_id", nullable = false)
    private Long tractoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_abastecimiento", nullable = false, length = 20)
    private TipoAbastecimiento tipoAbastecimiento;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "cantidad_galones", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidadGalones;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal kilometraje;

    @Column(name = "precio_unitario", precision = 12, scale = 4)
    private BigDecimal precioUnitario;

    @Column(name = "costo_total", precision = 14, scale = 2)
    private BigDecimal costoTotal;

    @Column(name = "tanque_origen", length = 100)
    private String tanqueOrigen;

    @Column(length = 150)
    private String proveedor;

    @Column(name = "numero_comprobante", length = 80)
    private String numeroComprobante;

    @Column(length = 500)
    private String observacion;
}