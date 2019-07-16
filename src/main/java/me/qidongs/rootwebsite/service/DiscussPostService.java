package me.qidongs.rootwebsite.service;

import me.qidongs.rootwebsite.dao.DiscussPostDao;
import me.qidongs.rootwebsite.model.DiscussPost;
import me.qidongs.rootwebsite.util.BadwordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostDao discussPostDao;

    @Autowired
    private BadwordsFilter badwordsFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostDao.selectDiscussPosts(userId,offset,limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostDao.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if (post==null){
            throw new IllegalArgumentException("argument cant be blank");
        }

        //
        //filter tags
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setTitle(HtmlUtils.htmlEscape(post.getContent()));

        //filter bad words
        post.setTitle(badwordsFilter.filter(post.getTitle()));
        post.setContent(badwordsFilter.filter(post.getContent()));

        return discussPostDao.insertDiscussPost(post);

    }

    public DiscussPost finddiscussPostById(int id){
        return discussPostDao.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount){return discussPostDao.updateCommentCount(id,commentCount);}


}

