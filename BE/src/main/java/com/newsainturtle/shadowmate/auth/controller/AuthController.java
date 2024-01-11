package com.newsainturtle.shadowmate.auth.controller;

import com.newsainturtle.shadowmate.auth.dto.*;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
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
    public ResponseEntity<BaseResponse> join(@RequestBody @Valid final JoinRequest joinRequest) {
        authServiceImpl.join(joinRequest);
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_JOIN));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse> login() {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_LOGIN));
    }

    @PostMapping("/auto-login")
    public ResponseEntity<BaseResponse> autoLogin(@RequestHeader("Auto-Login") final String key) {
        return ResponseEntity.ok().headers(authServiceImpl.checkAutoLogin(key)).body(BaseResponse.from(SUCCESS_AUTO_LOGIN));
    }

    @PostMapping("/nickname-duplicated")
    public ResponseEntity<BaseResponse> duplicatedCheckNickname(@RequestBody @Valid final DuplicatedNicknameRequest duplicatedNicknameRequest) {
        authServiceImpl.duplicatedCheckNickname(duplicatedNicknameRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_NICKNAME_CHECK));
    }

    @DeleteMapping("/nickname-duplicated")
    public ResponseEntity<BaseResponse> deleteCheckNickname(@RequestBody @Valid final DuplicatedNicknameRequest duplicatedNicknameRequest) {
        authServiceImpl.deleteCheckNickname(duplicatedNicknameRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_DELETE_NICKNAME_CHECK));
    }

    @PostMapping("/token/{userId}")
    public ResponseEntity<BaseResponse> changeToken(@RequestHeader("Authorization") final String token,
                                                    @PathVariable("userId") final Long userId,
                                                    @RequestBody @Valid final ChangeTokenRequest changeTokenRequest) {
        return ResponseEntity.ok().headers(authServiceImpl.changeToken(token, userId, changeTokenRequest)).body(BaseResponse.from(SUCCESS_CHANGE_ACCESS_TOKEN));
    }
}
