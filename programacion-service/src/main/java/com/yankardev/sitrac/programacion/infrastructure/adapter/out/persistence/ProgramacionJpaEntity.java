package com.yankardev.sitrac.programacion.infrastructure.adapter.out.persistence;
import com.yankardev.sitrac.programacion.domain.model.EstadoProgramacion;import jakarta.persistence.*;import lombok.*;import java.time.LocalDateTime;
@Entity @Table(name="programaciones") @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProgramacionJpaEntity{
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="pedido_id",nullable=false) private Long pedidoId;@Column(name="conductor_id",nullable=false) private Long conductorId;@Column(name="tracto_id",nullable=false) private Long tractoId;@Column(name="semirremolque_id",nullable=false) private Long semirremolqueId;
 @Column(name="fecha_programada",nullable=false) private LocalDateTime fechaProgramada;@Column(length=300) private String observacion;@Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EstadoProgramacion estado;
}