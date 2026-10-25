package com.yankardev.sitrac.notificacion.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConstanciaPdfService {

    public byte[] generar(
            Long viajeId,
            Long programacionId,
            Long pedidoId,
            String cliente,
            String fechaFinalizacion
    ) {
        List<String> lineas = List.of(
                "CONSTANCIA DE SERVICIO DE TRANSPORTE",
                "SITRAC - Sistema Integral de Transporte de Carga",
                "",
                "Cliente: " + valor(cliente),
                "Pedido: " + pedidoId,
                "Programación: " + programacionId,
                "Viaje: " + viajeId,
                "Fecha de finalización: " + valor(fechaFinalizacion),
                "Estado del servicio: FINALIZADO",
                "",
                "La presente constancia acredita el cierre del servicio de transporte",
                "registrado en SITRAC.",
                "",
                "Documento generado automáticamente por SITRAC."
        );

        StringBuilder contenido = new StringBuilder();
        contenido.append("BT\n");
        contenido.append("/F1 18 Tf\n");
        contenido.append("70 770 Td\n");

        boolean primera = true;
        for (String linea : lineas) {
            if (!primera) {
                contenido.append("0 -24 Td\n");
            }
            if (linea.isBlank()) {
                contenido.append("( ) Tj\n");
            } else {
                contenido.append("(").append(escape(linea)).append(") Tj\n");
            }
            if (primera) {
                contenido.append("/F1 12 Tf\n");
                primera = false;
            }
        }
        contenido.append("ET\n");

        return construirPdf(contenido.toString());
    }

    private byte[] construirPdf(String contenidoPagina) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        offsets.add(0);

        write(out, "%PDF-1.4\n%âãÏÓ\n");
        writeObject(out, offsets, 1, "<< /Type /Catalog /Pages 2 0 R >>");
        writeObject(out, offsets, 2, "<< /Type /Pages /Kids [3 0 R] /Count 1 >>");
        writeObject(out, offsets, 3,
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] "
                        + "/Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>");

        byte[] stream = contenidoPagina.getBytes(StandardCharsets.ISO_8859_1);
        offsets.add(out.size());
        write(out, "4 0 obj\n<< /Length " + stream.length + " >>\nstream\n");
        out.writeBytes(stream);
        write(out, "endstream\nendobj\n");

        writeObject(out, offsets, 5,
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");

        int xrefOffset = out.size();
        write(out, "xref\n0 6\n");
        write(out, "0000000000 65535 f \n");
        for (int i = 1; i <= 5; i++) {
            write(out, String.format("%010d 00000 n \n", offsets.get(i)));
        }
        write(out, "trailer\n<< /Size 6 /Root 1 0 R >>\n");
        write(out, "startxref\n" + xrefOffset + "\n%%EOF");
        return out.toByteArray();
    }

    private void writeObject(ByteArrayOutputStream out, List<Integer> offsets, int id, String body) {
        offsets.add(out.size());
        write(out, id + " 0 obj\n" + body + "\nendobj\n");
    }

    private void write(ByteArrayOutputStream out, String value) {
        out.writeBytes(value.getBytes(StandardCharsets.ISO_8859_1));
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)");
    }

    private String valor(String value) {
        return value == null || value.isBlank() ? "No especificado" : value;
    }
}
