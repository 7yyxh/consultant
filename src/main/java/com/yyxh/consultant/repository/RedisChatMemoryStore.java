package com.yyxh.consultant.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisChatMemoryStore implements ChatMemoryStore {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 获取Redis对应的会话数据
        String listJson = redisTemplate.opsForValue().get(memoryId.toString());
        // 转换并返回
        return ChatMessageDeserializer.messagesFromJson(listJson);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        // 将会话数据转换为json
        String listJson = ChatMessageSerializer.messagesToJson(list);
        // 存储到redis中
        redisTemplate.opsForValue().set(memoryId.toString(), listJson);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        redisTemplate.delete(memoryId.toString());
    }
}
