package com.newsainturtle.shadowmate.social.dto;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Getter
@Builder
public class SearchSocialResponse {

    private long socialId;

    private String socialImage;

    private Date dailyPlannerDay;

    private long userId;

    private String statusMessage;

    private String nickname;

    private String profileImage;

}
