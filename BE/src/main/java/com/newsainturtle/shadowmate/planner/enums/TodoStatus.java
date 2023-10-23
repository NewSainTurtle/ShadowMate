package com.newsainturtle.shadowmate.planner.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum TodoStatus {
    EMPTY("공백"),
    COMPLETE("완료"),
    INCOMPLETE("미완료"),
    ;

    private final String status;

    @JsonCreator
    public static TodoStatus parsing(String inputValue) {
        return Stream.of(TodoStatus.values())
                .filter(item -> item.status.equals(inputValue))
                .findFirst()
                .orElse(null);
    }
}
