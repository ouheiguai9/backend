package com.byakuya.boot.backend.api;

import com.byakuya.boot.backend.config.ApiModule;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@ApiModule(path = "callback", secure = false)
@Validated
class Callback {
    private final StringRedisTemplate stringRedisTemplate;

    Callback(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping("/{key}")
    public String redis(@PathVariable String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @GetMapping("/{key}/{value}")
    public Boolean redis(@PathVariable String key, @PathVariable String value) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value);
    }
}
