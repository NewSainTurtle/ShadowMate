package com.newsainturtle.shadowmate.planner.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ShareSocialResponse {

    private Long socialId;

    @Builder
    public ShareSocialResponse(Long socialId) {
        this.socialId = socialId;
    }
}
