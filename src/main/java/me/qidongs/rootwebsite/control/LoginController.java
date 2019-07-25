package me.qidongs.rootwebsite.control;

import com.google.code.kaptcha.Producer;
import me.qidongs.rootwebsite.dao.LoginTicketDao;
import me.qidongs.rootwebsite.service.UserService;
import me.qidongs.rootwebsite.util.CommunityConstant;
import me.qidongs.rootwebsite.util.CommunityUtil;
import me.qidongs.rootwebsite.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    UserService userService;

    @Autowired
    LoginTicketDao loginTicketDao;

    @Autowired
    private Producer producer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);


    @PostMapping(value = "/user/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Map<String,Object> map, HttpSession session){
        if(username.equals("admin")&& "123456".equals(password)){
            //login success

            session.setAttribute("loginUser",username);
            return "redirect:/main.html";
        }
        else{
            map.put("msg","Incorrect username/password");
            return "login";
        }

    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "site/login";
    }


    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }


    @PostMapping("/login")
    public String loginaction(String username, String password, String code,
                              Boolean rememberme, Model model,
                              HttpServletResponse response, @CookieValue("kaptchaOwner") String kaptchaOwner){

        //check kaptcha
        //String kaptcha =(String) session.getAttribute("kaptcha");
        String kaptcha = null;
        if (!StringUtils.isBlank(kaptchaOwner)){
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha= (String)redisTemplate.opsForValue().get(redisKey);
        }


        if (StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            System.out.println(code);
            model.addAttribute("codeMsg","Incorrect code");
            return "site/login";
        }

        //check account, password (from userservice)

        int expiredSeconds = rememberme == null ?   DEFAULT_EXPIRED_SECONDS: REMEMBER_EXPIRED_SECONDS;
        System.out.println("expired seconds"+expiredSeconds);
        Map<String,Object> map= userService.login(username,password,expiredSeconds);
        if (map.containsKey("ticket")){
            System.out.println("------>ticket got!");
            String temp = map.get("ticket").toString();
            System.out.println("what's inside ticket is:"+temp);
            System.out.println("context path is "+contextPath);
            Cookie cookie = new Cookie("ticket",temp);
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);

            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));

            return "site/login";
        }


    }


    @GetMapping(path="/kaptcha")
    public void getKaptcha(HttpServletResponse response){

        //get kaptcha
        String text =producer.createText();
        BufferedImage image = producer.createImage(text);

        //save to session
        //session.setAttribute("kaptcha",text);


        //Use cookie+redis instead of session
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);

        // save to redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey,text,60, TimeUnit.SECONDS);



        //output kaptcha to browser
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        }catch (IOException e){
            logger.error("failed Kaptcha" + e.getMessage());
        }

    }


}
