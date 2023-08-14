package com.newsainturtle.shadowmate.auth.controller;

import com.newsainturtle.shadowmate.auth.constant.AuthConstant;
import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.service.AuthServiceImpl;
import com.newsainturtle.shadowmate.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/api/auth/email-certificated")
    public ResponseEntity<BaseResponse> sendCertificationCode(@RequestBody @Valid CertifyEmailRequest certifyEmailRequest) {
        authServiceImpl.certifyEmail(certifyEmailRequest);
        return ResponseEntity.ok(BaseResponse.from(AuthConstant.SEND_CERTIFICATION_CODE));
    }

}
