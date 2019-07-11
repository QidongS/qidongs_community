package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.service.UserService;
import me.qidongs.rootwebsite.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    UserService userService;


    @Value("${server.servlet.context-path}")
    private String contextPath;

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
        return "/site/login";
    }


    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }


    @PostMapping("/login")
    public String loginaction(String username, String password, String code,
                              Boolean rememberme, Model model, HttpSession session,
                              HttpServletResponse response){

        //check kaptcha
        String kaptcha =(String) session.getAttribute("kaptcha");
        System.out.println("correct---->"+kaptcha);
        if (StringUtils.isBlank(kaptcha)||StringUtils.isBlank(code)||!kaptcha.equalsIgnoreCase(code)){
            System.out.println(code);
            model.addAttribute("codeMsg","Incorrect code");
            return "/site/login";
        }

        //check account, password (from userservice)

        int expiredSeconds = rememberme==null ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map= userService.login(username,password,expiredSeconds);
        if (map.containsKey("ticket")){
            System.out.println("------>ticket got!");
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);

            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));

            return "/site/login";
        }


    }

}
