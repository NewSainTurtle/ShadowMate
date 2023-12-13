package com.newsainturtle.shadowmate.user.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.newsainturtle.shadowmate.planner_setting.exception.PlannerSettingErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum PlannerAccessScope {
    PUBLIC("전체공개"),
    FOLLOW("친구공개"),
    PRIVATE("비공개"),
    ;

    private final String scope;

    @JsonCreator
    public static PlannerAccessScope parsing(String inputValue) {
        return Stream.of(PlannerAccessScope.values())
                .filter(item -> item.scope.equals(inputValue))
                .findFirst()
                .orElse(null);
    }
}
