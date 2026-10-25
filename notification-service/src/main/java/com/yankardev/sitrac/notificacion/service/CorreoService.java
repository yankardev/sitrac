package com.yankardev.sitrac.notificacion.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    private static final Logger log = LoggerFactory.getLogger(CorreoService.class);

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;

    public CorreoService(
            JavaMailSender mailSender,
            @Value("${sitrac.mail.enabled:false}") boolean enabled,
            @Value("${sitrac.mail.from:}") String from
    ) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    public void enviar(String destino, String asunto, String cuerpo) {
        if (destino == null || destino.isBlank()) {
            log.warn("No se envía correo: el cliente no tiene email registrado. Asunto={}", asunto);
            return;
        }

        if (!enabled) {
            log.info("[EMAIL DEMO] Para={} | Asunto={} | Mensaje={}", destino, asunto, cuerpo);
            return;
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        if (from != null && !from.isBlank()) {
            mensaje.setFrom(from);
        }
        mensaje.setTo(destino);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);

        log.info("Correo enviado a {} con asunto '{}'", destino, asunto);
    }
}
