package com.example.demo.modules.chat.service;

import com.example.demo.modules.chat.dto.ChatMessageDTO;
import com.example.demo.modules.chat.entity.ChatMessage;
import com.example.demo.modules.chat.repo.ChatMapper;
import com.example.demo.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {

    private static final Log logger = LogFactory.getLog(ChatService.class);

    @Autowired
    private final ChatMapper chatMapper;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    /**
     * Retrieves all chat messages.
     *
     * @return A list of {@link ChatMessage} objects.
     */
    public List<ChatMessage> getMessages() {
        logger.info("Fetching all chat messages.");
        List<ChatMessage> chatMessages = chatMapper.findAllMessages();
        logger.info("Fetched chat messages.");
        return chatMessages;
    }

    /**
     * Sends a new chat message.
     *
     * @param dto The {@link ChatMessageDTO} containing message details.
     * @return The created {@link ChatMessage} object.
     */
    public ChatMessage sendMessage(ChatMessageDTO dto) {
        logger.info("Received new message: " + dto);
        ChatMessage newMessage = new ChatMessage();
        newMessage.setChatId(idGeneratorService.generateChatMessageId());
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

    @Async
    public void deleteMessagesOlderThan(LocalDate today) {
        logger.info("Deleting chat messages older than: " + today);
        chatMapper.deleteMessagesOlderThan(today);
        logger.info("Deleted old chat messages.");
    }
}
