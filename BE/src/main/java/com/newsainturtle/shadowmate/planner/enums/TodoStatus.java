package com.newsainturtle.shadowmate.planner.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TodoStatus {
    EMPTY("공백"),
    COMPLETE("완료"),
    INCOMPLETE("미완료"),
    ;

    private final String status;
}
