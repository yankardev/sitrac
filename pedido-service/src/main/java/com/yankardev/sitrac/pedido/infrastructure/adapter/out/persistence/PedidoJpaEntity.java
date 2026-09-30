package com.yankardev.sitrac.pedido.infrastructure.adapter.out.persistence;

import com.yankardev.sitrac.pedido.domain.model.EstadoPedido;
import com.yankardev.sitrac.pedido.domain.model.TipoCarga;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="pedidos")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class PedidoJpaEntity {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="cliente_id", nullable=false)
    private Long clienteId;
    @Enumerated(EnumType.STRING) @Column(name="tipo_carga", nullable=false, length=30)
    private TipoCarga tipoCarga;
    @Column(name="descripcion_carga", length=200)
    private String descripcionCarga;
    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal toneladas;
    @Column(nullable=false, length=150)
    private String origen;
    @Column(nullable=false, length=150)
    private String destino;
    @Column(name="fecha_solicitud", nullable=false)
    private LocalDate fechaSolicitud;
    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private EstadoPedido estado;
}
