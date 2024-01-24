package com.newsainturtle.shadowmate.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileResponse {

    private String email;

    private String nickname;

    private String profileImage;

    private String statusMessage;

    private String plannerAccessScope;

    @Builder
    public ProfileResponse(String email, String nickname, String profileImage, String statusMessage, String plannerAccessScope) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.statusMessage = statusMessage;
        this.plannerAccessScope = plannerAccessScope;
    }
}
