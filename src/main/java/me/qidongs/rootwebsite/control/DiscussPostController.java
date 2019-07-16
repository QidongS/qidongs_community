package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.model.Comment;
import me.qidongs.rootwebsite.model.DiscussPost;
import me.qidongs.rootwebsite.model.Page;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.CommentService;
import me.qidongs.rootwebsite.service.DiscussPostService;
import me.qidongs.rootwebsite.service.UserService;
import me.qidongs.rootwebsite.util.CommunityConstant;
import me.qidongs.rootwebsite.util.CommunityUtil;
import me.qidongs.rootwebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

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

    //Post Content
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //post
        DiscussPost post =discussPostService.finddiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //find user name
        User user = userService.findUserById(post.getUserId());

        model.addAttribute("user",user);


        //page info
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());

        // comment(for post)
        List<Comment> commentList= commentService.findCommentsByEntity(ENTITY_TYPE_POST,post.getId(),page.getOffset(),page.getLimit());

        //comment view object list
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList!=null){
            for(Comment comment:commentList){
                //comment vo
                Map<String,Object> commentVo = new HashMap<>();
                //comment
                commentVo.put("comment",comment);
                //user
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                //comment(for comment)
                List<Comment> replyList= commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);

                List<Map<String,Object>> replyVoList = new ArrayList<>();

                if(replyList!=null){
                    for (Comment reply: replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        //reply
                        replyVo.put("reply",reply);
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //target
                        User target= reply.getTargetId()==0 ? null: userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);

                        replyVoList.add(replyVo);
                    }
                }

                //save reply
                commentVo.put("replys",replyVoList);

                //reply count
                int replyCount= commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVoList.add(commentVo);

            }
        }






        model.addAttribute("comments",commentVoList);


        return "/site/discuss-detail";
    }



}
