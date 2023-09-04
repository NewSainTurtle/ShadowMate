package com.newsainturtle.shadowmate.planner_setting.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlannerSettingErrorResult {
    UNREGISTERED_USER(HttpStatus.BAD_REQUEST, "등록되지 않은 사용자입니다."),
    INVALID_CATEGORY_COLOR(HttpStatus.BAD_REQUEST, "카테고리 색상 ID가 유효하지 않습니다."),
    INVALID_PLANNER_ACCESS_SCOPE(HttpStatus.BAD_REQUEST, "플래너 공개 범위 입력값이 유효하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
