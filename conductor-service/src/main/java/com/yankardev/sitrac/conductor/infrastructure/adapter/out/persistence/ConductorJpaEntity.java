package com.yankardev.sitrac.conductor.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "conductores",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_conductores_dni", columnNames = "dni"),
                @UniqueConstraint(name = "uk_conductores_licencia", columnNames = "numero_licencia")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConductorJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 8)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(name = "numero_licencia", nullable = false, length = 20)
    private String numeroLicencia;

    @Column(name = "categoria_licencia", nullable = false, length = 20)
    private String categoriaLicencia;

    @Column(name = "fecha_vencimiento_licencia", nullable = false)
    private LocalDate fechaVencimientoLicencia;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false)
    private boolean disponible;

    @Column(nullable = false)
    private boolean activo;
}
