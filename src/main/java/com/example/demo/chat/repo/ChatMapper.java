package com.example.demo.chat.repo;

import com.example.demo.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMapper {

    @Insert("INSERT INTO chat_messages(chat_id, user_id, avatar, username, current_level, message, sent_at) VALUES(#{chatId}, #{userId}, #{avatar}, #{username}, #{currentLevel}, #{message}, #{sentAt})")
    void createMessage(ChatMessage chatMessage);

    @Select("SELECT * FROM chat_messages WHERE DATE(sent_at) = CURDATE() ORDER BY sent_at")
    @Results({
            @Result(property = "chatId", column = "chat_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "currentLevel", column = "current_level"),
            @Result(property = "sentAt", column = "sent_at"),
    })
    List<ChatMessage> findAllMessages();

}
