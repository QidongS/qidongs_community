package me.qidongs.rootwebsite.util;

import me.qidongs.rootwebsite.model.User;
import org.springframework.stereotype.Component;

//replace session, thread safe
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear (){
        users.remove();
    }
}
