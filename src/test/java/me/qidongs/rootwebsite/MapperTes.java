package me.qidongs.rootwebsite;

import me.qidongs.rootwebsite.dao.UserDao;
import me.qidongs.rootwebsite.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RootwebsiteApplication.class)
public class MapperTes {

    @Autowired
    private UserDao userDao;

    @Test
    public void testSelectUser(){
        User user =userDao.selectById(101);
        System.out.println(user);

        user = userDao.selectByName("liubei");
        System.out.println(user);

        user=userDao.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("Goo");
        user.setPassword("randompassword");
        user.setSalt("agie");
        user.setEmail("test@gmail.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userDao.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdate(){
        int rows=userDao.updateStatus(150,1);
        System.out.println(rows);
        rows = userDao.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(rows);
        rows=userDao.updatePassword(150,"newpass");
    }

}
