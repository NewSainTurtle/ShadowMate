package com.newsainturtle.shadowmate.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateProfileImageRequest {

    private String newProfileImage;

    @Builder
    public UpdateProfileImageRequest(String newProfileImage) {
        this.newProfileImage = newProfileImage;
    }
}
