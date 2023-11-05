package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
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
        return (Boolean) redisTemplate.opsForValue().get("nickname " + key);
    }

    @Override
    @Transactional
    public void setHashEmailData(final String key, final EmailAuthentication value, final int timeout) {
        redisTemplate.opsForValue().set("email " + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void setHashNicknameData(String key, boolean value, int timeout) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                redisTemplate.multi();
                redisTemplate.opsForValue().set("nickname " + key, value, timeout, TimeUnit.MINUTES);
                return redisTemplate.exec();
            }
        });
    }

    @Override
    @Transactional
    public void deleteEmailData(final String key) {
        redisTemplate.opsForHash().delete("email", key);
    }

    @Override
    public void deleteNicknameData(String key) {
        redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                redisTemplate.multi();
                redisTemplate.delete("nickname " + key);
                return redisTemplate.exec();
            }
        });
    }
}
