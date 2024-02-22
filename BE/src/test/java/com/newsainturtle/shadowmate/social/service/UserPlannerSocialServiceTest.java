package com.newsainturtle.shadowmate.social.service;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.ShareSocialRequest;
import com.newsainturtle.shadowmate.planner.dto.response.ShareSocialResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerService;
import com.newsainturtle.shadowmate.social.dto.response.SearchSocialPlannerResponse;
import com.newsainturtle.shadowmate.social.exception.SocialErrorResult;
import com.newsainturtle.shadowmate.social.exception.SocialException;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.service.UserService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserPlannerSocialServiceTest extends DateCommonService {

    @InjectMocks
    private UserPlannerSocialServiceImpl userPlannerSocialService;

    @Mock
    private DailyPlannerService dailyPlannerService;

    @Mock
    private SocialService socialService;

    @Mock
    private UserService userService;

    private final String email = "yntest@shadowmate.com";
    private final String password = "yntest1234";
    private final String nickname = "거북이";
    private final SocialType socialType = SocialType.BASIC;
    private final PlannerAccessScope plannerAccessScope = PlannerAccessScope.PUBLIC;
    private final String date = "2023-09-25";
    private final User user = User.builder()
            .id(1L)
            .email(email)
            .password(password)
            .socialLogin(socialType)
            .nickname(nickname)
            .plannerAccessScope(plannerAccessScope)
            .withdrawal(false)
            .build();
    final DailyPlanner dailyPlanner = DailyPlanner.builder()
            .id(1L)
            .dailyPlannerDay(date)
            .user(user)
            .build();

    @Nested
    class 소셜공유 {
        final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";
        final ShareSocialRequest shareSocialRequest = ShareSocialRequest.builder()
                .date(date)
                .socialImage(socialImage)
                .build();

        @Test
        void 실패_공개상태가아닌경우() {
            //given
            final User user = User.builder()
                    .id(1L)
                    .email(email)
                    .password(password)
                    .socialLogin(socialType)
                    .nickname(nickname)
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();

            //when
            final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.shareSocial(user, shareSocialRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.FAILED_SHARE_SOCIAL);
        }

        @Test
        void 실패_유효하지않은플래너() {
            //given
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerService).getOrExceptionDailyPlanner(any(User.class), any(String.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerSocialService.shareSocial(user, shareSocialRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
        }

        @Test
        void 실패_이미공유한_소셜재공유() {
            //given
            doReturn(dailyPlanner).when(dailyPlannerService).getOrExceptionDailyPlanner(any(User.class), any(String.class));
            doThrow(new SocialException(SocialErrorResult.ALREADY_SHARED_SOCIAL)).when(socialService).shareSocial(any(User.class), any(DailyPlanner.class), any(String.class));

            //when
            final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.shareSocial(user, shareSocialRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.ALREADY_SHARED_SOCIAL);
        }

        @Test
        void 성공_소셜공유() {
            //given
            doReturn(dailyPlanner).when(dailyPlannerService).getOrExceptionDailyPlanner(any(User.class), any(String.class));
            doReturn(ShareSocialResponse.builder().socialId(1L).build()).when(socialService).shareSocial(any(User.class), any(DailyPlanner.class), any(String.class));

            //when
            ShareSocialResponse shareSocialResponse = userPlannerSocialService.shareSocial(user, shareSocialRequest);

            //then
            assertThat(shareSocialResponse).isNotNull();
            assertThat(shareSocialResponse.getSocialId()).isEqualTo(1L);
        }
    }

    @Nested
    class 소셜조회 {
        final String sort = "latest";
        final int pageNumber = 3;

        @Nested
        class 일반_소셜조회 {
            @Test
            void 실패_소셜조회공통_정렬입력값이잘못됨() {
                // given
                final String sort = "late";
                doThrow(new SocialException(SocialErrorResult.BAD_REQUEST_SORT)).when(socialService).getSocial(sort, pageNumber, "", "");

                // when
                final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.getSocial(sort, pageNumber, null, "", ""));

                // then
                assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.BAD_REQUEST_SORT);
            }

            @Test
            void 실패_잘못된날짜형식() {
                // given
                doThrow(new SocialException(SocialErrorResult.INVALID_DATE_FORMAT)).when(socialService).getSocial(sort, pageNumber, "2023.12.25", "2023.12.26");

                //when
                final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.getSocial(sort, pageNumber, null, "2023.12.25", "2023.12.26"));

                // then
                assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.INVALID_DATE_FORMAT);
            }

            @Test
            void 실패_잘못된날짜기간() {
                // given
                doThrow(new SocialException(SocialErrorResult.INVALID_DATE_PERIOD)).when(socialService).getSocial(sort, pageNumber, "2023-12-25", "2023-12-24");

                //when
                final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.getSocial(sort, pageNumber, null, "2023-12-25", "2023-12-24"));

                // then
                assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.INVALID_DATE_PERIOD);
            }

            @Test
            void 성공() {
                // given
                final SearchSocialPlannerResponse searchSocialPlannerResponse = SearchSocialPlannerResponse.builder()
                        .pageNumber(pageNumber)
                        .totalPage(4)
                        .sort(sort)
                        .socialList(new ArrayList<>())
                        .build();
                doReturn(searchSocialPlannerResponse).when(socialService).getSocial(sort, pageNumber, "", "");

                // when
                final SearchSocialPlannerResponse searchPublicDailyPlannerResponse = userPlannerSocialService.getSocial(sort, pageNumber, "", "", "");

                // then
                assertThat(searchPublicDailyPlannerResponse).isNotNull();
                assertThat(searchPublicDailyPlannerResponse.getTotalPage()).isEqualTo(4);
            }

            @Test
            void 성공_기간() {
                // given
                final SearchSocialPlannerResponse searchSocialPlannerResponse = SearchSocialPlannerResponse.builder()
                        .pageNumber(pageNumber)
                        .totalPage(4)
                        .sort(sort)
                        .socialList(new ArrayList<>())
                        .build();
                doReturn(searchSocialPlannerResponse).when(socialService).getSocial(sort, pageNumber, "2023-12-22", "2023-12-24");

                // when
                final SearchSocialPlannerResponse searchPublicDailyPlannerResponse = userPlannerSocialService.getSocial(sort, pageNumber, "", "2023-12-22", "2023-12-24");

                // then
                assertThat(searchPublicDailyPlannerResponse).isNotNull();
                assertThat(searchPublicDailyPlannerResponse.getTotalPage()).isEqualTo(4);
            }
        }

        @Nested
        class 닉네임검색_소셜조회 {

            @Test
            void 실패_소셜조회공통_정렬입력값이잘못됨() {
                // given
                final String sort = "late";
                doReturn(user).when(userService).getUserByNicknameAndScopePublic(nickname);
                doThrow(new SocialException(SocialErrorResult.BAD_REQUEST_SORT)).when(socialService).getSocial(sort, pageNumber, user, "", "");

                // when
                final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.getSocial(sort, pageNumber, nickname, "", ""));

                // then
                assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.BAD_REQUEST_SORT);
            }

            @Test
            void 실패_잘못된날짜형식() {
                // given
                doReturn(user).when(userService).getUserByNicknameAndScopePublic(nickname);
                doThrow(new SocialException(SocialErrorResult.INVALID_DATE_FORMAT)).when(socialService).getSocial(sort, pageNumber, user, "2023.12.25", "2023.12.26");

                //when
                final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.getSocial(sort, pageNumber, nickname, "2023.12.25", "2023.12.26"));

                // then
                assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.INVALID_DATE_FORMAT);
            }

            @Test
            void 실패_잘못된날짜기간() {
                // given
                doReturn(user).when(userService).getUserByNicknameAndScopePublic(nickname);
                doThrow(new SocialException(SocialErrorResult.INVALID_DATE_PERIOD)).when(socialService).getSocial(sort, pageNumber, user, "2023-12-25", "2023-12-24");

                //when
                final SocialException result = assertThrows(SocialException.class, () -> userPlannerSocialService.getSocial(sort, pageNumber, nickname, "2023-12-25", "2023-12-24"));

                // then
                assertThat(result.getErrorResult()).isEqualTo(SocialErrorResult.INVALID_DATE_PERIOD);
            }

            @Test
            void 성공_유저없음() {
                // given
                final SearchSocialPlannerResponse searchSocialPlannerResponse = SearchSocialPlannerResponse.builder()
                        .pageNumber(pageNumber)
                        .totalPage(1)
                        .sort(sort)
                        .socialList(new ArrayList<>())
                        .build();
                doReturn(null).when(userService).getUserByNicknameAndScopePublic(nickname);
                doReturn(searchSocialPlannerResponse).when(socialService).getSocial(sort, pageNumber, null, "", "");

                // when
                final SearchSocialPlannerResponse searchPublicDailyPlannerResponse = userPlannerSocialService.getSocial(sort, pageNumber, nickname, "", "");

                // then
                assertThat(searchPublicDailyPlannerResponse).isNotNull();
                assertThat(searchPublicDailyPlannerResponse.getTotalPage()).isEqualTo(1);
            }

            @Test
            void 성공() {
                // given
                final SearchSocialPlannerResponse searchSocialPlannerResponse = SearchSocialPlannerResponse.builder()
                        .pageNumber(pageNumber)
                        .totalPage(4)
                        .sort(sort)
                        .socialList(new ArrayList<>())
                        .build();
                doReturn(user).when(userService).getUserByNicknameAndScopePublic(nickname);
                doReturn(searchSocialPlannerResponse).when(socialService).getSocial(sort, pageNumber, user, "", "");

                // when
                final SearchSocialPlannerResponse searchPublicDailyPlannerResponse = userPlannerSocialService.getSocial(sort, pageNumber, nickname, "", "");

                // then
                assertThat(searchPublicDailyPlannerResponse).isNotNull();
                assertThat(searchPublicDailyPlannerResponse.getTotalPage()).isEqualTo(4);
            }

            @Test
            void 성공_기간() {
                // given
                final SearchSocialPlannerResponse searchSocialPlannerResponse = SearchSocialPlannerResponse.builder()
                        .pageNumber(pageNumber)
                        .totalPage(4)
                        .sort(sort)
                        .socialList(new ArrayList<>())
                        .build();
                doReturn(user).when(userService).getUserByNicknameAndScopePublic(nickname);
                doReturn(searchSocialPlannerResponse).when(socialService).getSocial(sort, pageNumber, user, "2023-12-22", "2023-12-24");

                // when
                final SearchSocialPlannerResponse searchPublicDailyPlannerResponse = userPlannerSocialService.getSocial(sort, pageNumber, nickname, "2023-12-22", "2023-12-24");

                // then
                assertThat(searchPublicDailyPlannerResponse).isNotNull();
                assertThat(searchPublicDailyPlannerResponse.getTotalPage()).isEqualTo(4);
            }
        }
    }
}
