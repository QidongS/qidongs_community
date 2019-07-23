package me.qidongs.rootwebsite.control.interceptor;

import me.qidongs.rootwebsite.model.LoginTicket;
import me.qidongs.rootwebsite.model.User;
import me.qidongs.rootwebsite.service.UserService;
import me.qidongs.rootwebsite.util.CookieUtil;
import me.qidongs.rootwebsite.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        //get ticket from cookies

        String ticket = CookieUtil.getValue(request, "ticket");



        if (ticket !=null){

            //find ticket
            LoginTicket loginTicket = userService.findLoginTicket(ticket);

            //check certificate expire
            if (loginTicket!=null && loginTicket.getStatus()==0 && loginTicket.getExpired().after(new Date()) ){
                //find such a user
                User user =userService.findUserById(loginTicket.getUserId());
                //save to holder (thread safe)
                hostHolder.setUser(user);
            }

        }


        return true;
    }

    //add user to model before template engine works (using modelAndView)
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        User user = hostHolder.getUser();
        if(user!=null && modelAndView!=null){

            modelAndView.addObject("loginUser",user);
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        hostHolder.clear();
    }
}
