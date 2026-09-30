package com.cris959.langchain4j_demo.dto;


public class ChatRequest {

    private String usuarioId;
    private String mensaje;

    // Getters y Setters
    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
