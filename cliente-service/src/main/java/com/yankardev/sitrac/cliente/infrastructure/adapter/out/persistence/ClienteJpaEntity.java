package com.yankardev.sitrac.cliente.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.cliente.domain.model.TipoDocumento;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "clientes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_clientes_numero_documento",
                columnNames = "numero_documento"
        )
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "nombre_razon_social", nullable = false, length = 150)
    private String nombreRazonSocial;

    @Column(length = 30)
    private String telefono;

    @Column(length = 120)
    private String email;

    @Column(length = 200)
    private String direccion;

    @Column(nullable = false)
    private boolean activo;
}
