package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.UserService;
import me.qidongs.rootwebsite.util.CommunityConstant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



import java.util.Map;

@Controller
public class RegisterController implements CommunityConstant {


    @Autowired
    private UserService userService;



    @GetMapping(value = "/register")
    public String getRegisterPage(){
        return "site/register";
    }




    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String,Object> map =userService.register(user);
        if (map==null || map.isEmpty()){
            //register success
            model.addAttribute("msg","Register Success! Please check your email for verification!");
            model.addAttribute("target","/index");
            return "site/operate-result";
        }
        else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "site/register";


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

        return "site/operate-result";
    }



}
