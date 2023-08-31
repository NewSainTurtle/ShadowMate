package com.newsainturtle.shadowmate.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DuplicatedNicknameRequest {

    @NotNull
    private String nickname;
}
