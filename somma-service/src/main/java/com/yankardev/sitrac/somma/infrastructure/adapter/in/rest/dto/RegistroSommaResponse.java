package com.yankardev.sitrac.somma.infrastructure.adapter.in.rest.dto;
import com.yankardev.sitrac.somma.domain.model.*;import java.time.LocalDateTime;
public record RegistroSommaResponse(Long id,TipoRegistroSomma tipo,Long conductorId,LocalDateTime fecha,String titulo,String descripcion,String lugar,EstadoRegistroSomma estado){}