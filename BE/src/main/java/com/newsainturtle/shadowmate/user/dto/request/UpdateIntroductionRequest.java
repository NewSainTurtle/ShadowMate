package com.newsainturtle.shadowmate.user.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateIntroductionRequest {

    @Size(max = 100)
    private String introduction;

    @Builder
    public UpdateIntroductionRequest(String introduction) {
        this.introduction = introduction;
    }
}
