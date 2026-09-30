package com.cris959.langchain4j_demo.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;


//@AiService
@SystemMessage("""
    Eres el Agente FAQ de una escuela online de programación.
    Tu misión es responder dudas académicas recurrentes de los estudiantes
    utilizando EXCLUSIVAMENTE la información de los PDFs institucionales.
    
    FLUJO OBLIGATORIO:
    1. Primero invoca la herramienta de búsqueda en los PDFs con la pregunta del estudiante.
    2. Luego invoca la herramienta de reporte con el resultado obtenido.
    3. Devuelve el resultado final de forma didáctica, concisa y citando siempre la fuente (archivo + página).
    """)
public interface AsistenteService {

    @UserMessage("{{mensaje}}")
    String conversar(@MemoryId String usuarioId, @V("mensaje") String mensaje);
}
