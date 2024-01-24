package com.newsainturtle.shadowmate.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {
    public static final String DATE_PATTERN = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$";
    public static final String DATE_PATTERN_ERROR_MESSAGE = "yyyy-MM-dd 형식의 올바른 날짜값이 아닙니다.";
    public static final String DATETIME_PATTERN = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])) ([01]\\d|2[0-3]):([0-5]0)$";
    public static final String DATETIME_PATTERN_ERROR_MESSAGE = "yyyy-MM-dd HH:mm 형식의 올바른값이 아닙니다.";
}
