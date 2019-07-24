package me.qidongs.rootwebsite.util;


public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE= "like:entity";
    private static final String PREFIX_USER_LIKE= "like:user";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_FOLLOWEE = "followee";


    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    //User's like
    //like:user:userId -> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    // Entity  User follows
    //followee:userId:entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+ entityType;
    }

    //Entity's follower
    //follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType + SPLIT +entityId;
    }

}
