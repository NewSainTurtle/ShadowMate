package com.newsainturtle.shadowmate.user.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.user.dto.ProfileResponse;
import com.newsainturtle.shadowmate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.newsainturtle.shadowmate.user.constant.UserConstant.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthService authService;

    @GetMapping("/{userId}/profiles")
    public ResponseEntity<BaseResponse> getProfile(@PathVariable Long userId) {
        ProfileResponse profileResponse = userService.getProfile(userId);
        return ResponseEntity.ok(BaseResponse.from(
                SUCCESS_PROFILE, profileResponse));
    }

    @GetMapping("/{userId}/searches")
    public ResponseEntity<BaseResponse> searchNickname(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestParam final String nickname) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(
                SUCCESS_SEARCH_NICKNAME, userService.searchNickname(principalDetails.getUser(), nickname)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<BaseResponse> deleteUser(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId) {
        authService.certifyUser(userId, principalDetails.getUser());
        userService.deleteUser(userId);
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_DELETE_USER));
    }
}
