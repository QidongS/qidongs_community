package me.qidongs.rootwebsite;

import me.qidongs.rootwebsite.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RootwebsiteApplication.class)
public class MailTEst {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("948813890sqd@gmail.com","TEST","Welcome");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "good morning");

        String content =templateEngine.process("/mail/demo",context);
        System.out.println(content);

        mailClient.sendMail("948813890sqd@gmail.com","HTML Email Test",content);

    }
}
