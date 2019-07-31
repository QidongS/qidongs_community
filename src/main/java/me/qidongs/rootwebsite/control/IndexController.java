package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.model.DiscussPost;
import me.qidongs.rootwebsite.model.Page;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.DiscussPostService;
import me.qidongs.rootwebsite.service.LikeService;
import me.qidongs.rootwebsite.service.UserService;
import me.qidongs.rootwebsite.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping({"/index","/"})
    public String index(Model model, Page page){
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list= discussPostService.findDiscussPosts(0,0,10);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list!=null){
            for(DiscussPost post : list){
                Map<String, Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);

                long likeCount = likeService.getEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);
                discussPosts.add(map);
            }

        }

        model.addAttribute("discussPosts",discussPosts);
        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage(){
        return "error/500";
    }
}
