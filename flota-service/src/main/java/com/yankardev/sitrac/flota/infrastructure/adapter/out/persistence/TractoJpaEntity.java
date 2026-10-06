package com.yankardev.sitrac.flota.infrastructure.adapter.out.persistence;
import com.yankardev.sitrac.flota.domain.model.EstadoUnidad; import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal;
@Entity @Table(name="tractos",uniqueConstraints=@UniqueConstraint(name="uk_tractos_placa",columnNames="placa"))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class TractoJpaEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=10) private String placa; @Column(nullable=false,length=60) private String marca; @Column(nullable=false,length=60) private String modelo;
 @Column(nullable=false) private Integer anio; @Column(name="capacidad_toneladas",nullable=false,precision=10,scale=2) private BigDecimal capacidadToneladas;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EstadoUnidad estado; @Column(nullable=false) private boolean activo;
}