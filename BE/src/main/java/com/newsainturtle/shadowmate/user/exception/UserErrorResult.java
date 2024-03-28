package com.newsainturtle.shadowmate.user.exception;

import com.newsainturtle.shadowmate.common.BaseErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorResult implements BaseErrorResult {

    NOT_FOUND_PROFILE(HttpStatus.NOT_FOUND, "프로필을 찾을 수 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    DIFFERENT_PASSWORD(HttpStatus.FORBIDDEN, "비밀번호가 다릅니다."),
    RETRY_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임 검사를 해야합니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}
