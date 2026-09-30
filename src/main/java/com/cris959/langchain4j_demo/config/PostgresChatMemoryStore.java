package com.cris959.langchain4j_demo.config;

import com.cris959.langchain4j_demo.entity.ChatMensajeEntity;
import com.cris959.langchain4j_demo.repository.ChatMensajeRepository;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostgresChatMemoryStore implements ChatMemoryStore {

    private final ChatMensajeRepository repository;

    public PostgresChatMemoryStore(ChatMensajeRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        List<ChatMensajeEntity> entities = repository.findByMemoryId(memoryId.toString());
        return entities.stream()
                .map(entity -> ChatMessageDeserializer.messageFromJson(entity.getContenidoJson()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        repository.deleteByMemoryId(memoryId.toString());
        List<ChatMensajeEntity> entities = messages.stream().map(msg -> {
            ChatMensajeEntity entity = new ChatMensajeEntity();
            entity.setMemoryId(memoryId.toString());
            entity.setTipoMensaje(msg.type().name());
            entity.setContenidoJson(ChatMessageSerializer.messageToJson(msg));
            return entity;
        }).collect(Collectors.toList());
        repository.saveAll(entities);
    }

    @Override
    @Transactional
    public void deleteMessages(Object memoryId) {
        repository.deleteByMemoryId(memoryId.toString());
    }
}