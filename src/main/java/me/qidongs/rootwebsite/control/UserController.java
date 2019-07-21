package me.qidongs.rootwebsite.control;

import me.qidongs.rootwebsite.annotation.LoginRequired;
import me.qidongs.rootwebsite.config.PathDomainConfig;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.UserService;
import me.qidongs.rootwebsite.util.CommunityUtil;
import me.qidongs.rootwebsite.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    PathDomainConfig pathDomainConfig;

    @Autowired
    private HostHolder hostHolder;

    @Value("server.servlet.context-path")
    private String contextPath;



    @LoginRequired
    @GetMapping(path = "/setting")
    public String getSettingPage(){
        return "site/setting";
    }


    @LoginRequired
    @PostMapping(path = "/upload")
    public String uploadHeader(MultipartFile profilePhoto, Model model){
        if(profilePhoto == null){
            model.addAttribute("error","You haven't select any image");
            return "site/setting";
        }

        String fileName = profilePhoto.getOriginalFilename();
        //check suffix
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","Incorrect file type");
            return "site/setting";

        }
        //generate random file name to avoid collision
        fileName = CommunityUtil.generateUUID()+suffix;
        //Ensure save path
        File path = new File(pathDomainConfig.getUpload()+"/"+fileName);
        try {
            //save file
            profilePhoto.transferTo(path);
        } catch (IOException e) {
            logger.error("upload failed"+e.getMessage());
            throw new RuntimeException("upload failed, server problem occurs",e);

        }

        //update current User's photo path
        User user = hostHolder.getUser();
        String photoUrl = pathDomainConfig.getIp()+contextPath+"/user/header/"+ fileName;
        System.out.println("external web "+ photoUrl);
        userService.updateProfilePhoto(user.getId(),photoUrl);



        return "redirect:index";
    }

    @GetMapping(path="/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String filename, HttpServletResponse response){
        //server save path
        filename= pathDomainConfig.getUpload()+ "/"+ filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try (FileInputStream files = new FileInputStream(filename);
             OutputStream os = response.getOutputStream();) {

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b=files.read(buffer))!=-1)
                os.write(buffer,0,b);
        } catch (IOException e) {
            logger.error("failed loading photo");
        }


    }


}
