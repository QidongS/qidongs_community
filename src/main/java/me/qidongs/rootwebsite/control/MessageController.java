package me.qidongs.rootwebsite.control;

import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer;
import me.qidongs.rootwebsite.model.Message;
import me.qidongs.rootwebsite.model.Page;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.MessageService;
import me.qidongs.rootwebsite.service.UserService;
import me.qidongs.rootwebsite.util.CommunityUtil;
import me.qidongs.rootwebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class MessageController {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //message list
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){//bean
        User user = hostHolder.getUser();
        //page info
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));

        //conversation list
        List<Map<String,Object>> conversations = new ArrayList<>();
        List<Message> conversationList = messageService.findConversation(user.getId(),page.getOffset(),page.getLimit());
        if (conversationList!=null){
            for (Message msg: conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation",msg);
                map.put("unreadCount",messageService.findLetterUnreadCount(user.getId(),msg.getConversationId()));
                map.put("letterCount",messageService.findLetterCount(msg.getConversationId()));
                int targetId= user.getId()==msg.getFromId() ? msg.getToId() : msg.getFromId();

                User temp =userService.findUserById(targetId);
                System.out.println(temp.getHeaderUrl());
                map.put("target",temp);
                //map.put("target",userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);

        //find unread count
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(),null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);


        return "/site/letter";

    }

    @GetMapping(path="/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Page page, Model model){
        //page info
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Message> letterList = messageService.findLetters(conversationId,page.getOffset(),page.getLimit());
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList!=null){
            for(Message message:letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }

        model.addAttribute("letters",letters);

        //find message target
        model.addAttribute("target",getLetterTarget(conversationId));


        //set read letter
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";

    }

    private User getLetterTarget(String conversationId){
        String[] ids= conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }
        else
            return userService.findUserById(id0);

    }


    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList !=null) {
            for(Message message : letterList){
                //for receiver && message unread
                if(hostHolder.getUser().getId() == message.getToId() && message.getStatus()==0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content){
        Message message = new Message();
        User target=  userService.findUserByName(toName);
        if(target==null){
            return CommunityUtil.getJSONString(1,"target not exist");
        }

        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setToId(target.getId());
        if (message.getFromId()< message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());

        }
        else{
            message.setConversationId(message.getToId()+"_" + message.getFromId());

        }

        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }
}
