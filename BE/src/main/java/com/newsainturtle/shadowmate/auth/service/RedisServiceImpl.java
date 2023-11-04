package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisServiceImpl implements RedisService{

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public EmailAuthentication getHashEmailData(final String key) {
        return (EmailAuthentication) redisTemplate.opsForValue().get("email " + key);
    }

    @Override
    public Boolean getHashNicknameData(String key) {
        return (Boolean) redisTemplate.opsForValue().get(key);
    }

    @Override
    @Transactional
    public void setHashEmailData(final String key, final EmailAuthentication value, final int timeout) {
        redisTemplate.opsForValue().set("email " + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void setHashNicknameData(String key, boolean value, int timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    @Transactional
    public void deleteEmailData(final String key) {
        redisTemplate.opsForHash().delete("email", key);
    }

    @Override
    public void deleteNicknameData(String key) {
        redisTemplate.opsForHash().delete(key);
    }
}
