package com.newsainturtle.shadowmate.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class DuplicatedNicknameRequest {

    @NotNull
    @Size(min = 2, max = 10)
    private String nickname;

    @Builder
    public DuplicatedNicknameRequest(String nickname) {
        this.nickname = nickname;
    }
}
