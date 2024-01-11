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
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final static String EMAIL = "email ";
    private final static String NICKNAME = "nickname ";

    @Override
    public EmailAuthentication getEmailData(final String key) {
        return (EmailAuthentication) redisTemplate.opsForValue().get(EMAIL + key);
    }

    @Override
    public Boolean getNicknameData(final String key) {
        return (Boolean) redisTemplate.opsForValue().get(NICKNAME + key);

    @Override
    @Transactional
    public void setEmailData(final String key, final EmailAuthentication value, final int timeout) {
        redisTemplate.opsForValue().set(EMAIL + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    @Transactional
    public void setNicknameData(final String key, final boolean value, final int timeout) {
        redisTemplate.opsForValue().set(NICKNAME + key, value, timeout, TimeUnit.MINUTES);
    }

    @Override
    @Transactional
    public void deleteEmailData(final String key) {
        redisTemplate.delete(EMAIL + key);
    }

    @Override
    @Transactional
    public void deleteNicknameData(final String key) {
        redisTemplate.delete(NICKNAME + key);
    }

}
