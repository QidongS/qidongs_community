package me.qidongs.rootwebsite.service;

import me.qidongs.rootwebsite.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    //
    public void like(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);

        //check current user like status from database
        //if has key userId then remove
        //if no such key then add a like
        boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);
        if (isMember){
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
        }
        else
        {
            redisTemplate.opsForSet().add(entityLikeKey,userId);
        }
    }

    //get like count
    public long getEntityLikeCount(int entityType,  int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //check current user like status from database (like/unlike/no)
    public int getEntityLikeStatus(int userId,int entityType,  int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);

        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1 : 0;


    }
}
