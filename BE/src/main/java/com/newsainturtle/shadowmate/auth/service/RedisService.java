package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;

public interface RedisService {

    EmailAuthentication getHashEmailData(final String key);
    void setHashEmailData(final String key, final EmailAuthentication value);
    void deleteEmailData(final String key);

}
