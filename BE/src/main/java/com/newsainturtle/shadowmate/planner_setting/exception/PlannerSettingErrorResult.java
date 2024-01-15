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
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리 ID가 유효하지 않습니다."),
    INVALID_DDAY(HttpStatus.BAD_REQUEST, "디데이 ID가 유효하지 않습니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "루틴 시작 날짜, 종료 날짜가 올바르지 않습니다."),
    INVALID_ROUTINE_DAY(HttpStatus.BAD_REQUEST, "루틴 요일값이 올바르지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
