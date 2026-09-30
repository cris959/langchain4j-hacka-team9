package com.cris959.langchain4j_demo.config;

import com.cris959.langchain4j_demo.service.AsistenteService;
import com.cris959.langchain4j_demo.tools.AsistenteTools;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.nio.file.Path;
import java.util.List;

@Configuration
public class AiConfig {

    private final ResourceLoader resourceLoader;

    @Value("${GROQ_BASE_URL}")
    private String groqBaseUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${GROQ_API_KEY}")
    private String groqApiKey;

    @Value("${GROQ_MODEL_NAME}")
    private String groqModelName;

    public AiConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl(groqBaseUrl)
                .apiKey(groqApiKey)
                .modelName(groqModelName)
                .temperature(0.7)
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        // Modelo local ligero para generar los vectores de tus documentos
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel) {
        EmbeddingStore<TextSegment> store = PgVectorEmbeddingStore.builder()
                .host("postgres") // para local # localhost #
                .port(5432)
                .database("langchain4j_demo")
                .user(dbUser)
                .password(dbPassword)
                .table("embeddings")
                .dimension(384)
                .build();

        try {
            Resource resource = resourceLoader.getResource("classpath:documents");
            if (resource.exists() && resource.getFile().exists()) {
                Path path = resource.getFile().toPath();

                // Cargamos los documentos pasando correctamente el parser de PDF de Apache PDFBox
                List<Document> documents = FileSystemDocumentLoader.loadDocuments(path, new ApachePdfBoxDocumentParser());

                if (!documents.isEmpty()) {
                    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                            .embeddingModel(embeddingModel)
                            .embeddingStore(store)
                            .build();
                    ingestor.ingest(documents);
                }
            }
        } catch (Exception e) {
            System.out.println("Aviso: No se pudo cargar la carpeta 'documents' en el arranque: " + e.getMessage());
        }

        return store;
    }

    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        // Recupera los fragmentos más relevantes basados en similitud vectorial
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.68)
                .build();
    }

    @Bean
    public AsistenteService asistenteService(ChatLanguageModel chatLanguageModel,
                                             AsistenteTools asistenteTools,
                                             ContentRetriever contentRetriever,
                                             PostgresChatMemoryStore postgresChatMemoryStore) {

        ChatMemoryProvider memoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(postgresChatMemoryStore)
                .build();

        return AiServices.builder(AsistenteService.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryProvider)
                .tools(asistenteTools)
                .contentRetriever(contentRetriever)
                .build();
    }
}