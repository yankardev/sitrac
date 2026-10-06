package com.yankardev.sitrac.programacion.infrastructure.adapter.in.rest.dto;
import com.yankardev.sitrac.programacion.domain.model.EstadoProgramacion;import java.time.LocalDateTime;
public record ProgramacionResponse(Long id,Long pedidoId,Long conductorId,Long tractoId,Long semirremolqueId,LocalDateTime fechaProgramada,String observacion,EstadoProgramacion estado){}