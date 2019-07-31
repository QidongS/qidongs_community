package me.qidongs.rootwebsite.event;

import com.alibaba.fastjson.JSONObject;
import me.qidongs.rootwebsite.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //handle event
    public void fireEvent(Event event){
        //fire the event to the topic
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));


    }
}
