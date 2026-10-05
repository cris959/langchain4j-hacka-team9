package com.cris959.langchain4j_demo.config;

import com.cris959.langchain4j_demo.service.AsistenteService;
import com.cris959.langchain4j_demo.tools.AsistenteTools;
import com.cris959.langchain4j_demo.tools.WebSearchTools;
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
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

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
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host("postgres") // o "localhost" según tu entorno
                .port(5432)
                .database("langchain4j_demo")
                .user(dbUser)
                .password(dbPassword)
                .table("embeddings")
                .dimension(384)
                .build();
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
    public WebSearchEngine webSearchEngine(@Value("${TAVILY_API_KEY}") String tavilyApiKey) {
        return TavilyWebSearchEngine.builder()
                .apiKey(tavilyApiKey)

                .build();
    }

    @Bean
    public AsistenteService asistenteService(ChatLanguageModel chatLanguageModel,
                                             AsistenteTools asistenteTools,
                                             WebSearchTools webSearchTools,
                                             PostgresChatMemoryStore postgresChatMemoryStore,
                                             ContentRetriever contentRetriever) {

        ChatMemoryProvider memoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10) // Mantiene la ventana acotada para evitar desbordes en PostgreSQL
                .chatMemoryStore(postgresChatMemoryStore)
                .build();

        return AiServices.builder(AsistenteService.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryProvider)
                .contentRetriever(contentRetriever)
                .tools(asistenteTools, webSearchTools)
                .build();
    }

    /**
     * Componente opcional para cargar los documentos al arrancar la app de forma segura.
     */
    @Bean
    public CommandLineRunner initDocuments(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        return args -> {
            try {
                org.springframework.core.io.support.ResourcePatternResolver resolver =
                        new org.springframework.core.io.support.PathMatchingResourcePatternResolver();

                // Busca todos los PDFs dentro de la carpeta documents en el classpath
                org.springframework.core.io.Resource[] resources = resolver.getResources("classpath:documents/**/*.pdf");

                List<dev.langchain4j.data.document.Document> documents = new java.util.ArrayList<>();

                for (org.springframework.core.io.Resource resource : resources) {
                    if (resource.exists() && resource.isReadable()) {
                        try (java.io.InputStream inputStream = resource.getInputStream()) {
                            dev.langchain4j.data.document.Document doc = new dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser().parse(inputStream);
                            documents.add(doc);
                        }
                    }
                }

                if (!documents.isEmpty()) {
                    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                            .embeddingModel(embeddingModel)
                            .embeddingStore(embeddingStore)
                            .build();
                    ingestor.ingest(documents);
                    System.out.println("-> [INFERENCIA] Documentos PDF cargados e ingeridos correctamente (" + documents.size() + " archivos).");
                } else {
                    System.out.println("-> [INFERENCIA] No se encontraron archivos PDF en 'classpath:documents'.");
                }
            } catch (Exception e) {
                System.out.println("Aviso: No se pudo cargar la carpeta 'documents' en el arranque: " + e.getMessage());
            }
        };
    }
}