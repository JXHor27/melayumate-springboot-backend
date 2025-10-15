package com.example.demo.chat.service;

import com.example.demo.chat.dto.ChatMessageDTO;
import com.example.demo.chat.entity.ChatMessage;
import com.example.demo.chat.repo.ChatMapper;
import com.example.demo.id.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {

    private static final Log logger = LogFactory.getLog(ChatService.class);


    @Autowired
    private final ChatMapper chatMapper;

    @Autowired
    private final IdGenerator idGenerator;


    public List<ChatMessage> getMessages() {
        logger.info("Fetching all chat messages.");

        List<ChatMessage> chatMessages = chatMapper.findAllMessages();

        logger.info("Fetched chat messages.");

        return chatMessages;
    }

    public ChatMessage sendMessage(ChatMessageDTO dto) {
        logger.info("Received new message: " + dto);

        ChatMessage newMessage = new ChatMessage();
        newMessage.setChatId(idGenerator.generateChatMessageId());
        newMessage.setUserId(dto.getUserId());
        newMessage.setAvatar(dto.getAvatar());
        newMessage.setUsername(dto.getUsername());
        newMessage.setCurrentLevel(dto.getCurrentLevel());
        newMessage.setMessage(dto.getMessage());
        newMessage.setSentAt(Instant.now());

        chatMapper.createMessage(newMessage);
        logger.info("Save new message: " + newMessage);

        return newMessage;
    }
}
