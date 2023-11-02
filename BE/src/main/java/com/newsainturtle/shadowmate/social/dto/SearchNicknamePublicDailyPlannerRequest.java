package com.newsainturtle.shadowmate.social.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchNicknamePublicDailyPlannerRequest {

    @NotNull
    @Size(min = 2, max = 10)
    private String nickname;

    @NotNull
    private String sort;

    @NotNull
    @Positive
    private long pageNumber;

    @Builder
    public SearchNicknamePublicDailyPlannerRequest(String nickname, String sort, long pageNumber) {
        this.nickname = nickname;
        this.sort = sort;
        this.pageNumber = pageNumber;
    }
}
