package com.cris959.langchain4j_demo.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;


//@AiService
public interface AsistenteService {

    @SystemMessage("""
        Eres el Agente FAQ de la escuela online CommunityLab.

        INSTRUCCIÓN DE ENRUTAMIENTO OBLIGATORIA:
        1. **Documentos Internos:** Utiliza la base de conocimiento vectorial EXCLUSIVAMENTE para responder sobre reglamentos, pagos, horarios o normas de CommunityLab.
        2. **Búsqueda Web (buscarEnInternet):** TIENES LA OBLIGACIÓN de usar esta herramienta siempre que te pregunten por tecnologías, librerías, frameworks o herramientas de desarrollo externas (ej: **LangChain4j**, **Spring AI**, **Spring Boot**, **Java**, errores de código o novedades técnicas).

        REQUISITO:
        - Si la pregunta es técnica (como la de LangChain4j), ESTÁS OBLIGADO A LLAMAR A LA HERRAMIENTA. No digas que no hay datos sin antes intentar buscar.
        """)
    @UserMessage("{{mensaje}}")
    String conversar(@MemoryId String usuarioId, @V("mensaje") String mensaje);
}