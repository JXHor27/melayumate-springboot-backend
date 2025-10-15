package com.example.demo.chat.controller;

import com.example.demo.chat.entity.ChatMessage;
import com.example.demo.chat.dto.ChatMessageDTO;
import com.example.demo.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    @Autowired
    private final ChatService chatService;

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Fetches all chat messages in chronological order.
     *
     * @return a ResponseEntity containing list of chat messages
     */
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> getMessages() {
        List<ChatMessage> messageList = chatService.getMessages();
        return ResponseEntity.ok(messageList);
    }

    /**
     * This method is called when a client sends a message to the "/app/chat" destination.
     * It saves the message to the database and then broadcasts it to all clients
     * subscribed to the "/topic/messages" topic.
     * 
     * @param chatMessageDTO The message payload from the client.
     * @return The message is not returned directly, but broadcasted.
     */
    @MessageMapping("/chat")
    public void processMessage(@Valid @Payload ChatMessageDTO chatMessageDTO) {

        // Save the message to the database and get back the message
        ChatMessage chatMessage = chatService.sendMessage(chatMessageDTO);

        // Broadcast the new message to all subscribed clients
        messagingTemplate.convertAndSend("/topic/messages", chatMessage);
    }
}
