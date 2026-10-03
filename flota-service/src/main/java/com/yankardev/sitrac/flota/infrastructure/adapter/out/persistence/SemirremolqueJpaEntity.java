package com.yankardev.sitrac.flota.infrastructure.adapter.out.persistence;
import com.yankardev.sitrac.flota.domain.model.*; import jakarta.persistence.*; import lombok.*; import java.math.BigDecimal;
@Entity @Table(name="semirremolques",uniqueConstraints=@UniqueConstraint(name="uk_semirremolques_placa",columnNames="placa"))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SemirremolqueJpaEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=10) private String placa; @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) private TipoSemirremolque tipo;
 @Column(name="capacidad_toneladas",nullable=false,precision=10,scale=2) private BigDecimal capacidadToneladas;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private EstadoUnidad estado; @Column(nullable=false) private boolean activo;
}