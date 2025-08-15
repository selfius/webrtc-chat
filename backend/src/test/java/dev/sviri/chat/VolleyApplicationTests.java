package dev.sviri.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;

import java.util.Date;
import java.util.Map;

@SpringBootTest
class VolleyApplicationTests {

    @Resource(name = "redisTemplate")
    RedisTemplate<String, String> redisTemplate;


    @Test
    void contextLoads() {
        record Nested(String some, String another) {
        }
        record Test(Integer field, String smhield, Date date, Nested nested) {
        }

        Map<String, Object> mapped = new Jackson2HashMapper(new ObjectMapper(), false)
                .toHash(new Test(42, "indeed", new Date(), new Nested("mep", "bloop")));
        System.out.println(mapped);
        redisTemplate.opsForHash().putAll("hash_test", mapped);
    }
}
