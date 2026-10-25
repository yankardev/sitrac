package com.yankardev.sitrac.notificacion.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

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
        if (!destinoValido(destino, asunto)) {
            return;
        }

        if (!enabled) {
            log.info("[EMAIL DEMO] Para={} | Asunto={} | Mensaje={}", destino, asunto, cuerpo);
            return;
        }

        SimpleMailMessage mensaje = new SimpleMailMessage();
        aplicarRemitente(mensaje);
        mensaje.setTo(destino);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);

        log.info("Correo enviado a {} con asunto '{}'", destino, asunto);
    }

    public void enviarConAdjunto(
            String destino,
            String asunto,
            String cuerpo,
            byte[] adjunto,
            String nombreAdjunto
    ) {
        if (!destinoValido(destino, asunto)) {
            return;
        }

        if (adjunto == null || adjunto.length == 0) {
            enviar(destino, asunto, cuerpo);
            return;
        }

        if (!enabled) {
            log.info("[EMAIL DEMO] Para={} | Asunto={} | Mensaje={} | Adjunto={} ({} bytes)",
                    destino, asunto, cuerpo, nombreAdjunto, adjunto.length);
            return;
        }

        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mensaje,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            if (from != null && !from.isBlank()) {
                helper.setFrom(from);
            }
            helper.setTo(destino);
            helper.setSubject(asunto);
            helper.setText(cuerpo, false);
            helper.addAttachment(nombreAdjunto, new ByteArrayResource(adjunto), "application/pdf");

            mailSender.send(mensaje);
            log.info("Correo con adjunto enviado a {} con asunto '{}'", destino, asunto);
        } catch (MessagingException ex) {
            throw new IllegalStateException("No se pudo preparar el correo con adjunto", ex);
        }
    }

    private boolean destinoValido(String destino, String asunto) {
        if (destino == null || destino.isBlank()) {
            log.warn("No se envía correo: no existe email registrado. Asunto={}", asunto);
            return false;
        }
        return true;
    }

    private void aplicarRemitente(SimpleMailMessage mensaje) {
        if (from != null && !from.isBlank()) {
            mensaje.setFrom(from);
        }
    }
}
