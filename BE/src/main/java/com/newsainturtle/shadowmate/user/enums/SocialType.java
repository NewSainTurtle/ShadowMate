package com.newsainturtle.shadowmate.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {

    BASIC("기본"),
    GOOGLE("구글"),
    ;
    private final String type;
}
