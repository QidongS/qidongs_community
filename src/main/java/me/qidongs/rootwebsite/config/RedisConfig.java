package me.qidongs.rootwebsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory) {
        //Spring container will inject factory automatically
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //set key serialize
        template.setKeySerializer(RedisSerializer.string());

        //set value serialize
        template.setValueSerializer(RedisSerializer.json());

        //set hash key serialize
        template.setHashKeySerializer(RedisSerializer.string());

        //set hash value serialize
        template.setHashValueSerializer(RedisSerializer.json());

        //make it work
        template.afterPropertiesSet();
        return template;
    }


}
