package com.cris959.langchain4j_demo.tools;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class AsistenteTools {

    private static final Logger log = LoggerFactory.getLogger(AsistenteTools.class);

    @Tool(value = "Busca en los PDFs institucionales de la escuela de programación y genera una respuesta basada en el contexto con citación de fuente (archivo y página).", name = "buscador")
    public String buscador(String preguntaEstudiante) {
        log.info("-> [HERRAMIENTA EJECUTADA] Buscador consultando documentos para: {}", preguntaEstudiante);
        // Aquí conectarás tu sistema RAG / EmbeddingStore (pgvector)
        return "[Contexto recuperado de manual_academico.pdf - Página 12]: Los trabajos prácticos tienen un plazo de entrega de 7 días posteriores a su publicación.";
    }

    @Tool(value = "Genera un reporte con la metadata del intento de respuesta del agente basándose en el resultado del buscador.", name = "reporte")
    public String reporte(String resultadoBuscador) {
        log.info("-> [HERRAMIENTA EJECUTADA] Generando reporte con la metadata del intento.");
        // Lógica de registro o auditoría del intento del agente
        return "Reporte registrado exitosamente en el sistema de auditoría académica.";
    }

    @Tool(value = "Calcula el promedio final dadas tres notas de exámenes parciales", name = "calcularPromedioNotas")
    public double calcularPromedioNotas(double nota1, double nota2, double nota3) {
        log.info("-> [HERRAMIENTA EJECUTADA] Calculando promedio final con las notas: {}, {}, {}", nota1, nota2, nota3);
        return (nota1 + nota2 + nota3) / 3.0;
    }

    @Tool(value = "Obtiene la hora actual del servidor. Útil cuando el usuario pregunta la hora o fecha actual.", name = "obtenerHoraActual")
    public String obtenerHoraActual() {
        log.info("-> [HERRAMIENTA EJECUTADA] Consultando la hora actual.");
        return java.time.LocalDateTime.now().toString();
    }
}