package com.cris959.langchain4j_demo.tools;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.web.search.WebSearchEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class WebSearchTools {

    private static final Logger log = LoggerFactory.getLogger(WebSearchTools.class);
    private final WebSearchEngine webSearchEngine;


    public WebSearchTools(WebSearchEngine webSearchEngine) {
        this.webSearchEngine = webSearchEngine;
    }


    @Tool("""
        Busca información actualizada en la web.
        REGLA ESTRICTA: Utiliza esta herramienta una sola vez por consulta de usuario.
        Si la búsqueda no devuelve resultados válidos, responde indicando que no hay datos disponibles.
    """)
    public String buscarEnInternet(String consulta) {
        try {
            log.info("-> [HERRAMIENTA WEB SEARCH EJECUTADA] Buscando: {}", consulta);
            var results = webSearchEngine.search(consulta).results();

            // Filtrar resultados que no tengan contenido útil (evitar los que vengan con null o vacíos)
            var resultadosValidos = results.stream()
                    .filter(r -> r.content() != null && !r.content().isBlank())
                    .limit(3)
                    .collect(Collectors.toList());

            if (resultadosValidos.isEmpty()) {
                log.warn("-> [WEB SEARCH] La búsqueda devolvió resultados pero sin contenido útil (null/vacio).");
                return "No se encontraron resultados con contenido válido en la web. DETENTE y responde al usuario que no hay información detallada disponible.";
            }

            String resultadoFormateado = resultadosValidos.stream()
                    .map(r -> "Título: " + r.title() + "\nURL: " + r.url() + "\nContenido: " + r.content())
                    .collect(Collectors.joining("\n---\n"));

            log.info("-> [WEB SEARCH RESULTADO VÁLIDO]: \n{}", resultadoFormateado);

            return resultadoFormateado;

        } catch (Exception e) {
            log.error("-> [WEB SEARCH ERROR]: {}", e.getMessage(), e);
            return "Error al realizar la búsqueda web: " + e.getMessage() + ". DETENTE.";
        }
    }
}