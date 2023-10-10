package com.newsainturtle.shadowmate.auth.controller;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
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

    @PostMapping("/email-certificated")
    public ResponseEntity<BaseResponse> sendCertificationCode(@RequestBody @Valid final CertifyEmailRequest certifyEmailRequest) {
        authServiceImpl.certifyEmail(certifyEmailRequest);
        return ResponseEntity.ok(BaseResponse.from(SEND_CERTIFICATION_CODE));
    }

    @PostMapping("/join")
    public ResponseEntity<BaseResponse> join(@RequestBody @Valid JoinRequest joinRequest) {
        authServiceImpl.join(joinRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_JOIN));
    }

    @PostMapping("/nickname-duplicated")
    public ResponseEntity<BaseResponse> duplicatedCheckNickname(@RequestBody @Valid DuplicatedNicknameRequest duplicatedNicknameRequest) {
        authServiceImpl.duplicatedCheckNickname(duplicatedNicknameRequest);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_NICKNAME_CHECK));
    }

    @GetMapping("/google")
    public String test() {
        return "구글로그인성공";
    }
}
