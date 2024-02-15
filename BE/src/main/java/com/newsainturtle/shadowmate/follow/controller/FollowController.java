package com.newsainturtle.shadowmate.follow.controller;

import com.newsainturtle.shadowmate.auth.service.AuthService;
import com.newsainturtle.shadowmate.common.BaseResponse;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.follow.dto.request.*;
import com.newsainturtle.shadowmate.follow.service.FollowServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @GetMapping("/{userId}/followers")
    public ResponseEntity<BaseResponse> getFollower(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                    @PathVariable("userId") final Long userId) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_GET_FOLLOWER_LIST, followService.getFollower(principalDetails.getUser())));
    }

    @GetMapping("/{userId}/receive-lists")
    public ResponseEntity<BaseResponse> getFollowRequestList(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                             @PathVariable("userId") final Long userId) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_GET_FOLLOW_REQUEST_LIST, followService.getFollowRequestList(principalDetails.getUser())));
    }

    @PostMapping("/{userId}/requested")
    public ResponseEntity<BaseResponse> addFollow(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                  @PathVariable("userId") final Long userId,
                                                  @RequestBody @Valid final AddFollowRequest addFollowRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_ADD_FOLLOW, followService.addFollow(principalDetails.getUser(), addFollowRequest.getFollowingId())));
    }

    @DeleteMapping("/{userId}/following")
    public ResponseEntity<BaseResponse> deleteFollowing(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                        @PathVariable("userId") final Long userId,
                                                        @RequestBody @Valid final DeleteFollowingRequest deleteFollowingRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        followService.deleteFollowing(principalDetails.getUser(), deleteFollowingRequest.getFollowingId());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_DELETE_FOLLOWING));
    }

    @DeleteMapping("/{userId}/followers")
    public ResponseEntity<BaseResponse> deleteFollower(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestBody @Valid final DeleteFollowerRequest deleteFollowerRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        followService.deleteFollower(principalDetails.getUser(), deleteFollowerRequest.getFollowerId());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_DELETE_FOLLOWER));
    }

    @DeleteMapping("/{userId}/requested")
    public ResponseEntity<BaseResponse> deleteFollowRequest(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                            @PathVariable("userId") final Long userId,
                                                            @RequestBody @Valid final DeleteFollowRequestRequest deleteFollowRequestRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        followService.deleteFollowRequest(principalDetails.getUser(), deleteFollowRequestRequest.getReceiverId());
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_DELETE_FOLLOW_REQUEST));
    }

    @PostMapping("/{userId}/receive")
    public ResponseEntity<BaseResponse> receiveFollow(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                      @PathVariable("userId") final Long userId,
                                                      @RequestBody @Valid final ReceiveFollowRequest receiveFollowRequest) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(followService.receiveFollow(principalDetails.getUser(), receiveFollowRequest.getRequesterId(), receiveFollowRequest.isFollowReceive())));
    }

    @GetMapping("/{userId}/counts")
    public ResponseEntity<BaseResponse> countFollow(@PathVariable("userId") final Long userId) {
        return ResponseEntity.ok(BaseResponse.from(SUCCESS_FOLLOW_COUNT, followService.countFollow(userId)));
    }


    @GetMapping("/{userId}/searches")
    public ResponseEntity<BaseResponse> searchNickname(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                       @PathVariable("userId") final Long userId,
                                                       @RequestParam final String nickname) {
        authService.certifyUser(userId, principalDetails.getUser());
        return ResponseEntity.ok(BaseResponse.from(
                SUCCESS_SEARCH_NICKNAME, followService.searchNickname(principalDetails.getUser(), nickname)));
    }

}
