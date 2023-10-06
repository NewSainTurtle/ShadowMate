package com.newsainturtle.shadowmate.follow.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.follow.constant.FollowConstant;
import com.newsainturtle.shadowmate.follow.dto.FollowingResponse;
import com.newsainturtle.shadowmate.follow.service.FollowService;
import com.newsainturtle.shadowmate.follow.service.FollowServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.newsainturtle.shadowmate.follow.constant.FollowConstant.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowServiceImpl followService;

    private final AuthService authService;

    @GetMapping("/{userId}/following")
    public ResponseEntity<BaseResponse> getFollowing(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                     @PathVariable("userId") final Long userId) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_GET_FOLLOWING_LIST, followService.getFollowing(principalDetails.getUser())));
    }

}