package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.model.DiscussPost;
import me.qidongs.rootwebsite.model.Page;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.DiscussPostService;
import me.qidongs.rootwebsite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @GetMapping({"/index","/"})
    //@ResponseBody
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
                discussPosts.add(map);
            }

        }

        model.addAttribute("discussPosts",discussPosts);
        return "index";
    }



}
