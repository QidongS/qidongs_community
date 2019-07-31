package me.qidongs.rootwebsite.service;

import me.qidongs.rootwebsite.config.PathDomainConfig;
import me.qidongs.rootwebsite.dao.LoginTicketDao;
import me.qidongs.rootwebsite.dao.UserDao;
import me.qidongs.rootwebsite.model.LoginTicket;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.util.CommunityConstant;
import me.qidongs.rootwebsite.util.CommunityUtil;
import me.qidongs.rootwebsite.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserDao userDao;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PathDomainConfig pathDomainConfig;

    @Autowired
    private LoginTicketDao loginTicketDao;



    @Value("${server.servlet.context-path}")
    private String contextPath;


    public User findUserById(int id){
        return userDao.selectById(id);
    }

    public User findUserByName(String name){return userDao.selectByName(name);}

    //user status
    public Map<String,Object> register (User user){
        Map<String,Object> map = new HashMap<>();

        if(user==null)
            throw new IllegalArgumentException("argument can't be null");
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","username can't be blank");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("usernameMsg","password can't be blank");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("emailMsg","email can't be blank");
            return map;
        }

        //verify account existence
        User u = userDao.selectByName(user.getUsername());
        if (u!=null) {
            map.put("usernameMsg","username already exists");
            return map;
        }

        //verify email existence
        u=userDao.selectByEmail(user.getEmail());
        if (u!=null)
        {
            map.put("emailMsg","email already exists");
            return map;
        }

        //register user (using tools)
        //add postfix: salt
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.generateMD5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userDao.insertUser(user);


        //send activation email!
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/101/code
        String url = this.pathDomainConfig.getIp() + contextPath + "/activation/" + user.getId()+"/"+user.getActivationCode();
        System.out.println("the URL is---------------->"+url);
        context.setVariable("url",url);
        String content = templateEngine.process("mail/activation",context);
        mailClient.sendMail(user.getEmail(),"Activate Account",content);



        return map;
    }




    public int activation(int userId, String code){
        User user = userDao.selectById(userId);
        if (user==null) return ACTIVATION_FAILURE;
        if (user.getStatus()==1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userDao.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else
            return ACTIVATION_FAILURE;

    }

    public Map<String,Object> login(String username, String password, int expiredseconds){
        Map<String,Object> map = new HashMap<>();

        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","account name can't be blank");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("usernameMsg","password can't be blank");
            return map;
        }

        //verify account
        User user = userDao.selectByName(username);
        if (user==null) {
            map.put("usernameMsg","username doesn't exist");
            return map;
        }

        //check account status (if email verified)
        if(user.getStatus()==0){
            map.put("usernameMsg","account not verified yet");
            return map;
        }

        password=CommunityUtil.generateMD5(password+user.getSalt());

        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","password incorrect!");
            return map;

        }


        //generate login ticket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        Date tempdate = new Date(System.currentTimeMillis()+expiredseconds*1000);

        loginTicket.setExpired(tempdate);

        loginTicket.setUserId(user.getId());

        loginTicketDao.insertLoginTicket(loginTicket);


        map.put("ticket",loginTicket.getTicket());


        return map;
    }

    public void logout(String ticket){
        loginTicketDao.updateStatus(ticket,1);
    }


    public LoginTicket findLoginTicket(String ticket){
        return loginTicketDao.selectByTicket(ticket);
    }


    public int updateProfilePhoto(int userId, String headrUrl){
        return userDao.updateHeader(userId,headrUrl);
    }
}
