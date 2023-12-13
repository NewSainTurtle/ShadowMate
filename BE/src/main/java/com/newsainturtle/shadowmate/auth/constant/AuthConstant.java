package com.newsainturtle.shadowmate.auth.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstant {
    public static final String SUCCESS_SEND_EMAIL_AUTHENTICATION_CODE = "이메일 인증코드 전송이 완료되었습니다.";

    public static final String SUCCESS_JOIN = "회원가입이 완료되었습니다.";

    public static final String SUCCESS_NICKNAME_CHECK = "닉네임이 사용가능합니다.";

    public static final String SUCCESS_DELETE_NICKNAME_CHECK = "닉네임 중복검증 삭제가 완료되었습니다.";

    public static final String SUCCESS_LOGIN = "로그인이 완료되었습니다.";
  
    public static final String SUCCESS_CHECK_EMAIL_AUTHENTICATION_CODE = "이메일 인증코드 확인이 완료되었습니다.";
}
