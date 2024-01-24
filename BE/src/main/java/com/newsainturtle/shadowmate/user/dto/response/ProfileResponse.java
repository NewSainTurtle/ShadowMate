package com.newsainturtle.shadowmate.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {
    private String email;
    private String nickname;
    private String profileImage;
    private String statusMessage;
    private String plannerAccessScope;
}
