package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.annotation.LoginRequired;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.LikeService;
import me.qidongs.rootwebsite.util.CommunityUtil;
import me.qidongs.rootwebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId){
        User user = hostHolder.getUser();
        //do upvote
        likeService.like(user.getId(),entityType,entityId);


        //get like count
        long likeCount = likeService.getEntityLikeCount(entityType,entityId);
        int likeStatus = likeService.getEntityLikeStatus(user.getId(),entityType,entityId);

        //to template
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        return CommunityUtil.getJSONString(0,null,map);

    }
}
