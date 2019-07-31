package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.annotation.LoginRequired;
import me.qidongs.rootwebsite.event.EventProducer;
import me.qidongs.rootwebsite.model.Comment;
import me.qidongs.rootwebsite.model.DiscussPost;
import me.qidongs.rootwebsite.model.Event;
import me.qidongs.rootwebsite.service.CommentService;
import me.qidongs.rootwebsite.service.DiscussPostService;
import me.qidongs.rootwebsite.util.CommunityConstant;
import me.qidongs.rootwebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer producer;

    @Autowired
    private DiscussPostService discussPostService;

    @LoginRequired
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable ("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //Fire Comment Event
        Event event = new Event().setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData("postId",discussPostId);

        if (comment.getEntityType()==ENTITY_TYPE_POST){
            DiscussPost target= discussPostService.finddiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());


        }else if(comment.getEntityType()==ENTITY_TYPE_COMMENT)
        {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        producer.fireEvent(event);

        return "redirect:/discuss/detail/"+discussPostId;
    }

}
