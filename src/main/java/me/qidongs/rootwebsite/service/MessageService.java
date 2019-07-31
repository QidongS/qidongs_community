package me.qidongs.rootwebsite.service;

import me.qidongs.rootwebsite.dao.MessageDao;
import me.qidongs.rootwebsite.model.Message;
import me.qidongs.rootwebsite.util.BadwordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;

    @Autowired
    BadwordsFilter badwordsFilter;

    public List<Message> findConversation(int userId, int offset, int limit){
        return messageDao.selectConversations(userId,offset,limit);
    }

    public int findConversationCount(int userId){
        return messageDao.selectConversationCount(userId);
    }

    public  List<Message> findLetters(String conversationId, int offset, int limit){
        return messageDao.selectLetters(conversationId,offset,limit);
    }

    public int findLetterCount(String conversationId){
        return messageDao.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId, String conversationId ){
        return messageDao.selectLetterUnreadCount(userId,conversationId);
    }

    public int addMessage(Message message){
        //bad word filter
        message.setContent(badwordsFilter.filter(HtmlUtils.htmlEscape(message.getContent()) ) );
        return messageDao.insertMessage(message);
    }

    //status from unread to read
    public int readMessage(List<Integer> ids){
        return messageDao.updateStatus(ids,1);
    }

    public Message getLatestNotice(int userId, String topic){
        return messageDao.selectLatestNotice(userId,topic);
    }

    public int findNoticeUnreadCount(int userId, String topic){
        return messageDao.selectNoticeUnreadCount(userId,topic);
    }

    public int findNoticeCount(int userId, String topic){
        return messageDao.selectNoticeCount(userId,topic);
    }

    public List<Message> findNotices(int userId, String topic, int offset, int limit){
        return messageDao.selectNotices(userId,topic,offset,limit);
    }

}
