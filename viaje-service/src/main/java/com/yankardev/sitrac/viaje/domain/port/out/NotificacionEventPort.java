package com.yankardev.sitrac.viaje.domain.port.out;

import java.time.LocalDateTime;

public interface NotificacionEventPort {
    void viajeFinalizado(Long viajeId, Long programacionId, LocalDateTime fechaFinalizacion);
}
