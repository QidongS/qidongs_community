package me.qidongs.rootwebsite.service;

import me.qidongs.rootwebsite.dao.UserDao;
import me.qidongs.rootwebsite.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public User findUserById(int id){
        return userDao.selectById(id);
    }
}
