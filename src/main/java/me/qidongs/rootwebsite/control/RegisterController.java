package me.qidongs.rootwebsite.control;

import com.google.code.kaptcha.Producer;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.UserService;
import me.qidongs.rootwebsite.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Map;

@Controller
public class RegisterController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer producer;

    @GetMapping(value = "/register")
    public String getRegisterPage(){
        return "/site/register";
    }




    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String,Object> map =userService.register(user);
        if (map==null || map.isEmpty()){
            //register success
            model.addAttribute("msg","Register Success! Please check your email for verification!");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";


        }
    }

    @GetMapping(path = "/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,
                             @PathVariable("code") String code){
        int result =userService.activation(userId,code);
        if (result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","Register Success! You are all set!");
            model.addAttribute("target","/login");

        }else if(result==ACTIVATION_REPEAT){
            model.addAttribute("msg","Register Success! Your account has already been activated!");
            model.addAttribute("target","/index");

        }else{
            model.addAttribute("msg","Register Failed! Invalid Activation!");
            model.addAttribute("target","/index");

        }

        return "/site/operate-result";
    }

    @GetMapping(path="/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){

        //get kaptcha
        String text =producer.createText();
        BufferedImage image = producer.createImage(text);

        //save to session
        session.setAttribute("kaptcha",text);

        //output to browser
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        }catch (IOException e){
            logger.error("failed Kaptcha" + e.getMessage());
        }

    }

}
