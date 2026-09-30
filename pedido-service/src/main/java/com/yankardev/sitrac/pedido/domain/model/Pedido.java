package com.yankardev.sitrac.pedido.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class Pedido {
    private Long id;
    private Long clienteId;
    private TipoCarga tipoCarga;
    private String descripcionCarga;
    private BigDecimal toneladas;
    private String origen;
    private String destino;
    private LocalDate fechaSolicitud;
    private EstadoPedido estado;
}
