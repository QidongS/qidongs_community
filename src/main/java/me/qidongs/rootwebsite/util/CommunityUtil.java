package me.qidongs.rootwebsite.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

//tools
public class CommunityUtil {
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5 encode
    public static String generateMD5(String key){
        if (StringUtils.isBlank(key))
            return null;

        //key(string)->key(byte)--MD5-->string(hex)
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
