package com.newsainturtle.shadowmate.social.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchSocialResponse {

    private long socialId;

    private String socialImage;

    private String dailyPlannerDay;

    private long userId;

    private String statusMessage;

    private String nickname;

    private String profileImage;

}
