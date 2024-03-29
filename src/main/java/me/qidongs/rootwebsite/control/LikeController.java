package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.annotation.LoginRequired;
import me.qidongs.rootwebsite.event.EventProducer;
import me.qidongs.rootwebsite.model.Event;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.LikeService;
import me.qidongs.rootwebsite.util.CommunityConstant;
import me.qidongs.rootwebsite.util.CommunityUtil;
import me.qidongs.rootwebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer producer;

    //@LoginRequired
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId){
        User user = hostHolder.getUser();
        //do upvote
        likeService.like(user.getId(),entityType,entityId,entityUserId);


        //get like count
        long likeCount = likeService.getEntityLikeCount(entityType,entityId);
        int likeStatus = likeService.getEntityLikeStatus(user.getId(),entityType,entityId);

        //to template
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        //fire like event if user likes
        if(likeStatus==STATUS_LIKE){
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            producer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(0,null,map);

    }
}
