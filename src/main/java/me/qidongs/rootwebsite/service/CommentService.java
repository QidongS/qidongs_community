package me.qidongs.rootwebsite.service;

import me.qidongs.rootwebsite.dao.CommentDao;
import me.qidongs.rootwebsite.model.Comment;
import me.qidongs.rootwebsite.util.BadwordsFilter;
import me.qidongs.rootwebsite.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private BadwordsFilter badwordsFilter;

    @Autowired
    private DiscussPostService discussPostService;


    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit){
        return commentDao.selectCommentsByEntity(entityType,entityId,offset,limit);

    }

    public int findCommentCount(int entityType, int entityId){
        return commentDao.selectCountByEntity(entityType,entityId);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        //filter comment before insert
        if(comment==null){
            throw new IllegalArgumentException("argument can't be blank");
        }
        comment.setContent(badwordsFilter.filter(HtmlUtils.htmlEscape(comment.getContent()) ));
        int rows = commentDao.insertComment(comment);

        if(comment.getEntityType()==ENTITY_TYPE_POST){
            int count= commentDao.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }
        return rows;
        //
    }

    public Comment findCommentById(int id){
        return commentDao.selectCommentById(id);
    }
}
