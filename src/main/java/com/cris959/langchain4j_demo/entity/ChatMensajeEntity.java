package com.cris959.langchain4j_demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "chat_mensajes")
@Getter @Setter
public class ChatMensajeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memoryId;

    @Column(columnDefinition = "TEXT")
    private String tipoMensaje;  // USER, AI, etc

    @Column(columnDefinition = "TEXT")
    private String contenidoJson;
}
