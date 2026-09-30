package com.cris959.langchain4j_demo.repository;

import com.cris959.langchain4j_demo.entity.ChatMensajeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMensajeRepository extends JpaRepository<ChatMensajeEntity, Long>{
    List<ChatMensajeEntity> findByMemoryId(String memoryId);
    void deleteByMemoryId(String memoryId);
}