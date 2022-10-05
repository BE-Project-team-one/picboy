package com.sparta.picboy.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class CertificationDao {
    private final String PREFIX = "sms:";  // 1
    private final int LIMIT_TIME = 3 * 60;  // 2

    private final StringRedisTemplate stringRedisTemplate;

    public void createSmsCertification(String phoneNum, String numStr) { // 3
        stringRedisTemplate.opsForValue()
                .set(PREFIX + phoneNum, numStr, Duration.ofSeconds(LIMIT_TIME));
    }

    public String getSmsCertification(String numStr) { // 4
        return stringRedisTemplate.opsForValue().get(PREFIX + numStr);
    }

    public void removeSmsCertification(String phoneNum) { // 5
        stringRedisTemplate.delete(PREFIX + phoneNum);
    }

    public boolean hasKey(String phoneNum) {  // 6
        return stringRedisTemplate.hasKey(PREFIX + phoneNum);
    }
}
