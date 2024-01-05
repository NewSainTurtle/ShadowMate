package com.newsainturtle.shadowmate.social.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.social.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.newsainturtle.shadowmate.social.constant.SocialConstant.*;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {

    private final AuthService authService;

    private final SocialService socialService;

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse> getSocial(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                  @PathVariable("userId") final Long userId,
                                                  @RequestParam final String sort,
                                                  @RequestParam(name = "page-number") final Integer pageNumber,
                                                  @RequestParam final String nickname,
                                                  @RequestParam(name = "start-date") final String startDate,
                                                  @RequestParam(name = "end-date") final String endDate) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_SOCIAL, socialService.getSocial(sort, pageNumber, nickname, startDate, endDate)));
    }

    @DeleteMapping("/{userId}/{socialId}")
    public ResponseEntity<BaseResponse> deleteSocial(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId,
                                                     @PathVariable("socialId") final Long socialId) {
        authService.certifyUser(userId, principalDetails.getUser());
        socialService.deleteSocial(socialId);
        return ResponseEntity.accepted().body(BaseResponse.from(SUCCESS_DELETE_SOCIAL));
    }
}
