package com.yankardev.sitrac.programacion.domain.port.out;

import java.time.LocalDateTime;

public interface NotificacionEventPort {
    void programacionCreada(Long programacionId, Long pedidoId, LocalDateTime fechaProgramada);
}
