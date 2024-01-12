package com.newsainturtle.shadowmate.auth.service;

import com.newsainturtle.shadowmate.auth.entity.EmailAuthentication;

public interface RedisService {

    EmailAuthentication getEmailData(final String key);
    Boolean getNicknameData(final String key);
    String getRefreshTokenData(final Long userId, final String type);
    void setEmailData(final String key, final EmailAuthentication value, final int timeout);
    void setNicknameData(final String key, final boolean value, final int timeout);
    void setRefreshTokenData(final Long userId, final String type, final String refreshToken, final int timeout);
    void deleteEmailData(final String key);
    void deleteNicknameData(final String key);
    
}
