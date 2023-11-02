package com.newsainturtle.shadowmate.social.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchNicknamePublicDailyPlannerRequest {

    private String nickname;

    private String sort;

    private long pageNumber;

    @Builder
    public SearchNicknamePublicDailyPlannerRequest(String nickname, String sort, long pageNumber) {
        this.nickname = nickname;
        this.sort = sort;
        this.pageNumber = pageNumber;
    }
}
