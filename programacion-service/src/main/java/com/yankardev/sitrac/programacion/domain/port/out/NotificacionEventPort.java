package com.yankardev.sitrac.programacion.domain.port.out;

import java.time.LocalDate;

public interface NotificacionEventPort {
    void programacionCreada(Long programacionId, Long pedidoId, LocalDate fechaProgramada);
}
