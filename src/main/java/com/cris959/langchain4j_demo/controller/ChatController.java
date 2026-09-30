package com.cris959.langchain4j_demo.controller;

import com.cris959.langchain4j_demo.dto.ChatRequest;
import com.cris959.langchain4j_demo.service.AsistenteService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/chat")
public class ChatController {

    private final AsistenteService asistenteService;

    public ChatController(AsistenteService asistenteService) {
        this.asistenteService = asistenteService;
    }
    @GetMapping
    public String chatear(@RequestParam String usuarioId, @RequestParam String mensaje) {
        return asistenteService.conversar(usuarioId, mensaje);
    }

    @PostMapping
    public String chatearPost(@RequestBody ChatRequest request) {
        return asistenteService.conversar(request.getUsuarioId(), request.getMensaje());
    }
}