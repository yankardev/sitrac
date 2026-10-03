package com.yankardev.sitrac.mantenimiento.domain.model;
import lombok.*;import java.math.BigDecimal;import java.time.LocalDate;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class Mantenimiento{private Long id,unidadId;private TipoUnidad tipoUnidad;private TipoMantenimiento tipoMantenimiento;private LocalDate fechaInicio,fechaFin;private String descripcion;private BigDecimal costo;private EstadoMantenimiento estado;}