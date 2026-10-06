package com.yankardev.sitrac.programacion.domain.model;
import lombok.*; import java.time.LocalDateTime;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class Programacion{private Long id,pedidoId,conductorId,tractoId,semirremolqueId;private LocalDateTime fechaProgramada;private String observacion;private EstadoProgramacion estado;}