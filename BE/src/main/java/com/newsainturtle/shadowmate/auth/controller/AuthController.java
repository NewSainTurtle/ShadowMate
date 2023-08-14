package com.newsainturtle.shadowmate.auth.controller;

import com.newsainturtle.shadowmate.auth.dto.CertifyEmailRequest;
import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

}
