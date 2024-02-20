package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.planner.dto.request.AddDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.response.SearchVisitorBookResponse;
import com.newsainturtle.shadowmate.planner.dto.response.VisitorBookResponse;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.MonthlyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.UserPlannerServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.exception.UserErrorResult;
import com.newsainturtle.shadowmate.user.exception.UserException;
import com.newsainturtle.shadowmate.user.service.UserServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPlannerServiceTest {

    @InjectMocks
    private UserPlannerServiceImpl userPlannerService;

    @Mock
    private DailyPlannerServiceImpl dailyPlannerService;

    @Mock
    private MonthlyPlannerServiceImpl monthlyPlannerService;

    @Mock
    private UserServiceImpl userService;

    private final User user = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .email("jntest@shadowmate.com")
            .password("yntest1234")
            .socialLogin(SocialType.BASIC)
            .nickname("토끼")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();
    private final String date = "2023-09-25";

    @Nested
    class 방명록 {
        private final long ownerId = user.getId();

        @Nested
        class 방명록추가 {
            private final String visitorBookContent = "왔다가유 @--";
            final AddVisitorBookRequest addVisitorBookRequest = AddVisitorBookRequest.builder()
                    .visitorBookContent(visitorBookContent)
                    .build();

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(ownerId);

                //when
                final UserException result = assertThrows(UserException.class, () -> userPlannerService.addVisitorBook(user2, ownerId, addVisitorBookRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
            }

            @Test
            void 실패_자신플래너에방명록추가() {
                //given
                final long visitorId = user2.getId();
                doReturn(user2).when(userService).getUserById(visitorId);
                doThrow(new PlannerException(PlannerErrorResult.FAILED_SELF_VISITOR_BOOK_WRITING)).when(monthlyPlannerService).addVisitorBook(user2, user2, addVisitorBookRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.addVisitorBook(user2, visitorId, addVisitorBookRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.FAILED_SELF_VISITOR_BOOK_WRITING);
            }

            @Test
            void 성공() {
                //given
                final VisitorBookResponse visitorBookResponse = VisitorBookResponse.builder()
                        .visitorBookId(1L)
                        .visitorId(user2.getId())
                        .visitorNickname(user2.getNickname())
                        .visitorProfileImage(user2.getProfileImage())
                        .visitorBookContent("안녕하세요")
                        .build();
                doReturn(user).when(userService).getUserById(ownerId);
                doReturn(visitorBookResponse).when(monthlyPlannerService).addVisitorBook(user2, user, addVisitorBookRequest);

                //when
                final VisitorBookResponse addVisitorBookResponse = userPlannerService.addVisitorBook(user2, ownerId, addVisitorBookRequest);

                //then
                assertThat(addVisitorBookResponse).isNotNull();
                assertThat(addVisitorBookResponse.getVisitorBookId()).isEqualTo(1L);

                //verify
                verify(userService, times(1)).getUserById(any(Long.class));
                verify(monthlyPlannerService, times(1)).addVisitorBook(any(User.class), any(User.class), any(AddVisitorBookRequest.class));
            }
        }

        @Nested
        class 방명록조회 {

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(ownerId);

                //when
                final UserException result = assertThrows(UserException.class, () -> userPlannerService.searchVisitorBook(user2, ownerId, 0L));

                //then
                assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
            }

            @Test
            void 성공() {
                //given
                final long visitorId = user2.getId();
                doReturn(user2).when(userService).getUserById(visitorId);
                doReturn(SearchVisitorBookResponse.builder().visitorBookResponses(new ArrayList<>()).build()).when(monthlyPlannerService).searchVisitorBook(user2, user2, 0L);

                //when
                final SearchVisitorBookResponse searchVisitorBookResponse = userPlannerService.searchVisitorBook(user2, visitorId, 0L);

                //then
                assertThat(searchVisitorBookResponse).isNotNull();

                //verify
                verify(userService, times(1)).getUserById(any(Long.class));
                verify(monthlyPlannerService, times(1)).searchVisitorBook(any(User.class), any(User.class), any(Long.class));
            }
        }
    }

    @Nested
    class 좋아요 {
        final Long plannerWriterId = 1L;

        @Nested
        class 좋아요등록 {
            final AddDailyLikeRequest addDailyLikeRequest = AddDailyLikeRequest.builder()
                    .date(date)
                    .build();

            @Test
            void 실패_자신플래너에좋아요() {
                //given
                doReturn(user).when(userService).getUserById(plannerWriterId);
                doThrow(new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER)).when(dailyPlannerService).addDailyLike(user, user, addDailyLikeRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.addDailyLike(user, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
            }

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(plannerWriterId);

                //when
                final UserException result = assertThrows(UserException.class, () -> userPlannerService.addDailyLike(user2, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
            }

            @Test
            void 실패_유효하지않은플래너() {
                //given
                doReturn(user).when(userService).getUserById(plannerWriterId);
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerService).addDailyLike(user2, user, addDailyLikeRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.addDailyLike(user2, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            void 실패_이전에좋아요를이미누름() {
                //given
                doReturn(user).when(userService).getUserById(plannerWriterId);
                doThrow(new PlannerException(PlannerErrorResult.ALREADY_ADDED_LIKE)).when(dailyPlannerService).addDailyLike(user2, user, addDailyLikeRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.addDailyLike(user2, plannerWriterId, addDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.ALREADY_ADDED_LIKE);
            }

            @Test
            void 성공() {
                //given
                doReturn(user).when(userService).getUserById(plannerWriterId);

                //when
                userPlannerService.addDailyLike(user2, plannerWriterId, addDailyLikeRequest);

                //then

                //verify
                verify(userService, times(1)).getUserById(any(Long.class));
                verify(dailyPlannerService, times(1)).addDailyLike(any(User.class), any(User.class), any(AddDailyLikeRequest.class));

            }
        }

        @Nested
        class 좋아요취소 {
            final RemoveDailyLikeRequest removeDailyLikeRequest = RemoveDailyLikeRequest.builder()
                    .date(date)
                    .build();

            @Test
            void 실패_자신플래너에좋아요취소() {
                //given
                doReturn(user).when(userService).getUserById(plannerWriterId);
                doThrow(new PlannerException(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER)).when(dailyPlannerService).removeDailyLike(user, user, removeDailyLikeRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.removeDailyLike(user, plannerWriterId, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.UNABLE_TO_LIKE_YOUR_OWN_PLANNER);
            }

            @Test
            void 실패_유효하지않은사용자() {
                //given
                doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(plannerWriterId);

                //when
                final UserException result = assertThrows(UserException.class, () -> userPlannerService.removeDailyLike(user2, plannerWriterId, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
            }

            @Test
            void 실패_유효하지않은플래너() {
                //given
                doReturn(user).when(userService).getUserById(plannerWriterId);
                doThrow(new PlannerException(PlannerErrorResult.INVALID_DAILY_PLANNER)).when(dailyPlannerService).removeDailyLike(user2, user, removeDailyLikeRequest);

                //when
                final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.removeDailyLike(user2, plannerWriterId, removeDailyLikeRequest));

                //then
                assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DAILY_PLANNER);
            }

            @Test
            void 성공() {
                //given
                doReturn(user).when(userService).getUserById(plannerWriterId);

                //when
                userPlannerService.removeDailyLike(user2, plannerWriterId, removeDailyLikeRequest);

                //then

                //verify
                verify(userService, times(1)).getUserById(any(Long.class));
                verify(dailyPlannerService, times(1)).removeDailyLike(any(User.class), any(User.class), any(RemoveDailyLikeRequest.class));
            }
        }
    }

}
