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
    public static final String SUCCESS_LOGOUT = "로그아웃이 완료되었습니다.";
    public static final String SUCCESS_AUTO_LOGIN = "자동로그인이 완료되었습니다.";
    public static final String SUCCESS_CHECK_EMAIL_AUTHENTICATION_CODE = "이메일 인증코드 확인이 완료되었습니다.";
    public static final String SUCCESS_CHANGE_ACCESS_TOKEN = "액세스 토큰 재발급이 완료되었습니다.";

    public static final String REDIS_KEY_EMAIL = "email ";
    public static final String REDIS_KEY_NICKNAME = "nickname ";
    public static final String REDIS_KEY_REFRESH = "refresh ";
    public static final String REDIS_KEY_AUTOLOGIN = "autoLogin ";

    public static final String MESSAGE_SUBJECT = "ShadowMate 회원가입 인증 코드";
    public static final String MESSAGE_FRONT = "<div style='margin:10;'> <div align='center' style='border:1px solid black; font-family:verdana';> <h3 style='color:blue;'>회원가입 코드입니다.</h3> <div style='font-size:130%'> CODE : <strong>";
    public static final String MESSAGE_BACK = "</strong><div><br/> </div>";

}
