package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.model.DiscussPost;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.DiscussPostService;
import me.qidongs.rootwebsite.util.CommunityUtil;
import me.qidongs.rootwebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJSONString(403, "You need to login first");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setContent(content);
        post.setTitle(title);
        post.setCreateTime(new Date());

        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0,"Publish Success!");

    }
}
