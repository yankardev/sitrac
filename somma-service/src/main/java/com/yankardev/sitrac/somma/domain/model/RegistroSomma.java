package com.yankardev.sitrac.somma.domain.model;
import lombok.*;import java.time.LocalDateTime;
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class RegistroSomma{private Long id,conductorId;private TipoRegistroSomma tipo;private LocalDateTime fecha;private String titulo,descripcion,lugar;private EstadoRegistroSomma estado;}