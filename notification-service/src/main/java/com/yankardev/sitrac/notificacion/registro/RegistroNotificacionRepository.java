package com.yankardev.sitrac.notificacion.registro;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroNotificacionRepository extends JpaRepository<RegistroNotificacion, Long> {
    List<RegistroNotificacion> findAllByOrderByFechaRegistroDesc();
}
