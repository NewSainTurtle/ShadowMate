package com.newsainturtle.shadowmate.auth.controller;

import com.newsainturtle.shadowmate.auth.dto.SendEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.dto.CheckEmailAuthenticationCodeRequest;
import com.newsainturtle.shadowmate.auth.dto.DuplicatedNicknameRequest;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.newsainturtle.shadowmate.auth.constant.AuthConstant.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authServiceImpl;

    @PostMapping("/email-authentication")
    public ResponseEntity<BaseResponse> sendEmailAuthenticationCode(@RequestBody @Valid final SendEmailAuthenticationCodeRequest sendEmailAuthenticationCodeRequest) {
        authServiceImpl.sendEmailAuthenticationCode(sendEmailAuthenticationCodeRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEND_EMAIL_AUTHENTICATION_CODE));
    }

    @PostMapping("/email-authentication/check")
    public ResponseEntity<BaseResponse> checkEmailAuthenticationCode(@RequestBody @Valid final CheckEmailAuthenticationCodeRequest checkEmailAuthenticationCodeRequest) {
        authServiceImpl.checkEmailAuthenticationCode(checkEmailAuthenticationCodeRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_CHECK_EMAIL_AUTHENTICATION_CODE));
    }

    @PostMapping("/join")
    public ResponseEntity<BaseResponse> join(@RequestBody @Valid JoinRequest joinRequest) {
        authServiceImpl.join(joinRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_JOIN));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login() {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_LOGIN));
    }

    @PostMapping("/nickname-duplicated")
    public ResponseEntity<BaseResponse> duplicatedCheckNickname(@RequestBody @Valid DuplicatedNicknameRequest duplicatedNicknameRequest) {
        authServiceImpl.duplicatedCheckNickname(duplicatedNicknameRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_NICKNAME_CHECK));
    }
}
