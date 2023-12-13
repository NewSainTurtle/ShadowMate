package com.newsainturtle.shadowmate.social.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.social.dto.SearchNicknamePublicDailyPlannerRequest;
import com.newsainturtle.shadowmate.social.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.newsainturtle.shadowmate.social.constant.SocialConstant.*;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController {

    private final AuthService authService;

    private final SocialService socialService;

    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse> searchPublicDailyPlanner(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                                 @PathVariable("userId") final Long userId,
                                                                 @RequestParam final String sort,
                                                                 @RequestParam final Long pageNumber) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_PUBLIC_DAILY_PLANNER, socialService.searchPublicDailyPlanner(sort, pageNumber)));
    }

    @PostMapping("/{userId}/searches/nicknames")
    public ResponseEntity<BaseResponse> searchNicknamePublicDailyPlanner(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                                         @PathVariable("userId") final Long userId,
                                                                         @RequestBody @Valid final SearchNicknamePublicDailyPlannerRequest searchNicknamePublicDailyPlannerRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_SEARCH_NICKNAME_PUBLIC_DAILY_PLANNER, socialService.searchNicknamePublicDailyPlanner(searchNicknamePublicDailyPlannerRequest)));
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
