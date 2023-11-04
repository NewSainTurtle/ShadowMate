package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;

public interface RedisService {

    EmailAuthentication getHashEmailData(final String key);
    Boolean getHashNicknameData(final String key);
    void setHashEmailData(final String key, final EmailAuthentication value, final int timeout);
    void setHashNicknameData(final String key, final boolean value, final int timeout);
    void deleteEmailData(final String key);
    void deleteNicknameData(final String key);

}
