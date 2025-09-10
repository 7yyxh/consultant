package com.yyxh.consultant.config;

import com.yyxh.consultant.repository.RedisChatMemoryStore;
import com.yyxh.consultant.service.ConsultantService;
import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class CommonConfig {

    @Autowired
    private OpenAiChatModel model;
    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private RedisEmbeddingStore redisEmbeddingStore;

//    @Bean
//    public ConsultantService consultantService() {
//        ConsultantService cs = AiServices.builder(ConsultantService.class)
//                .chatModel(model)
//                .build();
//        return cs;
//    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        return new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(redisChatMemoryStore)
                        .build();
            }
        };
    }

    // 构建向量数据库操作对象
    @Bean
    @Primary
    public EmbeddingStore store() {
        // 1. 读取文档数据转换为Document数组
        List<Document> documents = ClassPathDocumentLoader.loadDocuments("content");
        // 2. 创建内存型向量数据库操作对象
//        InMemoryEmbeddingStore store = new InMemoryEmbeddingStore();
        // 3.1. 创建文档分割器
        DocumentSplitter ds = DocumentSplitters.recursive(500,100);
        // 3.2. 创建能完成数据切割、向量化、存储的对象
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(redisEmbeddingStore)  // 设置数据库操作对象
                .documentSplitter(ds)   // 设置文档分割器
                .embeddingModel(embeddingModel)
                .build();
        // 设置文档数据
        ingestor.ingest(documents);
        return redisEmbeddingStore;
    }

    // 构建向量数据库检索对象
    @Bean
    public ContentRetriever contentRetriever(/*EmbeddingStore store*/){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(redisEmbeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)  // 取前n个文本片段
                .minScore(0.6)  // 余弦相似度最低为0.6
                .build();
    }

}
