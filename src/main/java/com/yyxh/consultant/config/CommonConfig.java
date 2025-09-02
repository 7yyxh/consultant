package com.yyxh.consultant.config;

import com.yyxh.consultant.repository.RedisChatMemoryStore;
import com.yyxh.consultant.service.ConsultantService;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Autowired
    private OpenAiChatModel model;
    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;

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


}
