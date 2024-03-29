package me.qidongs.rootwebsite;

import me.qidongs.rootwebsite.dao.DiscussPostDao;
import me.qidongs.rootwebsite.dao.LoginTicketDao;
import me.qidongs.rootwebsite.dao.MessageDao;
import me.qidongs.rootwebsite.dao.UserDao;
import me.qidongs.rootwebsite.model.DiscussPost;
import me.qidongs.rootwebsite.model.LoginTicket;
import me.qidongs.rootwebsite.model.Message;
import me.qidongs.rootwebsite.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RootwebsiteApplication.class)
public class MapperTes {

    @Autowired
    private LoginTicketDao loginTicketDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DiscussPostDao discussPostDao;

    @Autowired
    private MessageDao messageDao;
    @Test
    public void testSelectUser(){
        User user =userDao.selectById(101);
        System.out.println(user);

        user = userDao.selectByName("liubei");
        System.out.println(user);

        user=userDao.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("Goo");
        user.setPassword("randompassword");
        user.setSalt("agie");
        user.setEmail("test@gmail.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userDao.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdate(){
        int rows=userDao.updateStatus(150,1);
        System.out.println(rows);
        rows = userDao.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(rows);
        rows=userDao.updatePassword(150,"newpass");
    }

    @Test
    public void SelectPosts(){
//        List<DiscussPost> list =discussPostDao.selectDiscussPosts(0,0,10);
//        for (DiscussPost discussPost:list){
//            System.out.println(discussPost);
//        }
        discussPostDao.selectDiscussPostRows(12);

    }

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setStatus(0);
        loginTicket.setTicket("abc");
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60));
        loginTicketDao.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket;
        loginTicket=loginTicketDao.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateLoginTicket(){
        LoginTicket loginTicket;
        loginTicketDao.updateStatus("abc",1);
    }

    @Test
    public void testDiscussSelectById(){
        //DiscussPost discussPost= discussPostDao.selectDiscussPostById(234);
        discussPostDao.selectDiscussPostById(234);
        //System.out.println(discussPost);
    }

    @Test
    public void testMessageSelect(){
        List<Message> list= messageDao.selectConversations(111,0,20);
        for(Message m: list){
            System.out.println(m);
        }


        int count = messageDao.selectConversationCount(111);
        System.out.println(count);

//        list= messageDao.selectLetters("111_112",0,10);
//        for(Message m: list){
//            System.out.println(m);
//        }
//
//        int count =messageDao.selectLetterCount("111_112");
//        System.out.println(count);

        System.out.println(messageDao.selectLetterUnreadCount(131,"111_131"));
    }
}
