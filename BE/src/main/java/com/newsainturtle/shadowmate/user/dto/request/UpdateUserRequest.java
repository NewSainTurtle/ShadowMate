package com.newsainturtle.shadowmate.user.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateUserRequest {

    @NotNull
    private String newNickname;
    private String newProfileImage;
    private String newStatusMessage;

    @Builder
    public UpdateUserRequest(String newNickname, String newProfileImage, String newStatusMessage) {
        this.newNickname = newNickname;
        this.newProfileImage = newProfileImage;
        this.newStatusMessage = newStatusMessage;
    }
}
