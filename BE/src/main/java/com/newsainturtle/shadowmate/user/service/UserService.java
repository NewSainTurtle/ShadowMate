package com.newsainturtle.shadowmate.user.service;

import com.newsainturtle.shadowmate.user.dto.ProfileResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ProfileResponse getProfile(final Long userId);

}
