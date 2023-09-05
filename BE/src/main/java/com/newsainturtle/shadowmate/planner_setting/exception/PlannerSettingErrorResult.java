package com.newsainturtle.shadowmate.planner_setting.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlannerSettingErrorResult implements BaseErrorResult {
    INVALID_CATEGORY_COLOR(HttpStatus.BAD_REQUEST, "카테고리 색상 ID가 유효하지 않습니다."),
    INVALID_PLANNER_ACCESS_SCOPE(HttpStatus.BAD_REQUEST, "플래너 공개 범위 입력값이 유효하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
