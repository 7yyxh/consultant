package com.yyxh.consultant.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

//@AiService
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
        streamingChatModel = "openAiStreamingChatModel",
//        chatMemory = "chatMemory"
        chatMemoryProvider = "chatMemoryProvider",
        contentRetriever = "contentRetriever"
)
public interface ConsultantService {

    /**
     *  聊天接口
     * @param msg 用户输入内容
     * @return  模型响应内容
     */
    @SystemMessage(fromResource = "system.txt")
//    @UserMessage("你是牛魔王!{{kj}}")
    Flux<String> chatStreaming(@MemoryId String memoryId, @UserMessage String msg);

    String chat(String message);
}
