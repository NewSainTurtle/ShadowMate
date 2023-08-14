package com.newsainturtle.shadowmate.user.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlannerAccessScope {
    PUBLIC("전체공개"),
    FOLLOW("친구공개"),
    PRIVATE("비공개"),
    ;

    private final String scope;
}
