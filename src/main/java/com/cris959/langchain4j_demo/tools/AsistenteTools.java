package com.cris959.langchain4j_demo.tools;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AsistenteTools {

    private static final Logger log = LoggerFactory.getLogger(AsistenteTools.class);

    @Tool("Calcula el costo total de un producto aplicando un porcentaje de descuento dado. Útil para consultas de precios.")
    public double calcularDescuento(double precioOriginal, double porcentajeDescuento) {
        log.info("-> [HERRAMIENTA EJECUTADA] Calculando descuento para precio: {}", precioOriginal);
        if (porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }
        return precioOriginal - (precioOriginal * (porcentajeDescuento / 100.0));
    }

    @Tool("Obtiene la hora actual del servidor. Útil cuando el usuario pregunta la hora o fecha actual.")
    public String obtenerHoraActual() {
        log.info("-> [HERRAMIENTA EJECUTADA] Consultando la hora actual.");
        return java.time.LocalDateTime.now().toString();
    }

    @Tool("Consulta el estado académico de un alumno por su número de documento o ID")
    public String consultarEstadoAcademico(String usuarioId) {
        log.info("-> [HERRAMIENTA EJECUTADA] Consultando el estado académico para el usuarioId: {}", usuarioId);
        // Simulación de una consulta a base de datos de alumnos
        // Aquí podrías inyectar un Repositorio real de Spring Data JPA si lo deseas
        Random random = new Random();
        boolean alDia = random.nextBoolean();

        if (alDia) {
            return "El alumno con ID " + usuarioId + " se encuentra al día con sus cuotas y documentación académica.";
        } else {
            return "El alumno con ID " + usuarioId + " presenta documentación pendiente en secretaría académica.";
        }
    }

    @Tool("Calcula el promedio final dadas tres notas de exámenes parciales")
    public double calcularPromedioNotas(double nota1, double nota2, double nota3) {
        log.info("-> [HERRAMIENTA EJECUTADA] Calculando promedio final con las notas: {}, {}, {}", nota1, nota2, nota3);
        return (nota1 + nota2 + nota3) / 3.0;
    }
}