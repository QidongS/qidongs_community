package me.qidongs.rootwebsite;

import me.qidongs.rootwebsite.util.BadwordsFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = RootwebsiteApplication.class)
public class BadWordTest {

    @Autowired
    private BadwordsFilter badwordsFilter;

    @Test
    public void testFilter(){
        String text = "good morning mother fucker book dirty, some time computer fuck apple random words filter";
        text=badwordsFilter.filter(text);
        System.out.println(text);
    }
}
