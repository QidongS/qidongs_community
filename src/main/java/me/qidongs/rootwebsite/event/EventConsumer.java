package me.qidongs.rootwebsite.event;

import com.alibaba.fastjson.JSONObject;
import me.qidongs.rootwebsite.model.Event;
import me.qidongs.rootwebsite.model.Message;
import me.qidongs.rootwebsite.service.MessageService;
import me.qidongs.rootwebsite.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleMessages(ConsumerRecord record){
        if (record==null || record.value()==null){
            logger.error("Message Null Message");
            return ;

        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (event==null){
            logger.error("Message Format Incorrect");
        }

        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityUserId());

        if(!event.getData().isEmpty()){
            for (Map.Entry<String, Object> entry: event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());

            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);

    }
}
