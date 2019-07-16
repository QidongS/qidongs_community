package me.qidongs.rootwebsite.dao;

import me.qidongs.rootwebsite.model.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface DiscussPostDao {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    int selectDiscussPostRows(@Param("userId") int userId);



    int insertDiscussPost(DiscussPost discussPost);


    DiscussPost selectDiscussPostById(int id);

    //database has duplicate field comment count in table discusspost
    //update it when new comment is inserted
    int updateCommentCount(int id, int commentCount);

}
