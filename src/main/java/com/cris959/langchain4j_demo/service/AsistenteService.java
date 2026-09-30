package com.cris959.langchain4j_demo.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;


public interface AsistenteService {

    @UserMessage("Eres un asistente tercnico experto en desarrollo backend. Responde de forma clara y concisa a la siguiente consulta: {{mensaje}}")
    String conversar(@MemoryId String usuarioId, @V("mensaje") String mensaje);
}
