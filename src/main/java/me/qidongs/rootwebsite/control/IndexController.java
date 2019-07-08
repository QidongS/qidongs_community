package me.qidongs.rootwebsite.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @GetMapping({"/index","/"})
    //@ResponseBody
    public String index(){
        return "index";
        //return "Hello there,";
    }



}
