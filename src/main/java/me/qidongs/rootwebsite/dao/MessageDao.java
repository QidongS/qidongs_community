package me.qidongs.rootwebsite.dao;

import me.qidongs.rootwebsite.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MessageDao {
    //1.find conversation list for current user (only return the latest one message)
    List<Message> selectConversations(int userId, int offset, int limit);

    //2.find current user's message count
    int selectConversationCount (int userId);

    //3. find the letter list that's included by a conversation
    List<Message> selectLetters(String conversationId, int offset, int limit);


    //4. find the number of letters from a conversation
    int selectLetterCount(String conversationId);

    //5. find unread count with(conversationId provided/ none)
    int selectLetterUnreadCount(int userId, String conversationId);

    //6. add a new message
    int insertMessage(Message message);

    //7. helper updateStatus (e.g. when new message is added)
    int updateStatus(List<Integer> ids, int status);
}
