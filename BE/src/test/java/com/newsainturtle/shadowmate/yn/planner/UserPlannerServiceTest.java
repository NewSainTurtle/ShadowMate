package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.follow.service.FollowService;
import com.newsainturtle.shadowmate.planner.dto.request.AddDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.request.AddVisitorBookRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveDailyLikeRequest;
import com.newsainturtle.shadowmate.planner.dto.response.*;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.service.DailyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.MonthlyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.UserPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner.service.WeeklyPlannerService;
import com.newsainturtle.shadowmate.planner_setting.service.PlannerSettingService;
import com.newsainturtle.shadowmate.planner_setting.service.RoutineService;
import com.newsainturtle.shadowmate.social.service.SocialService;
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

import java.time.LocalDate;
import java.time.YearMonth;
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

    @Mock
    private PlannerSettingService plannerSettingService;

    @Mock
    private FollowService followService;

    @Mock
    private WeeklyPlannerService weeklyPlannerService;

    @Mock
    private RoutineService routineService;

    @Mock
    private SocialService socialService;

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

    @Nested
    class 일일플래너조회 {
        final long userId = 1L;

        @Test
        void 실패_유효하지않은날짜형식() {
            //given
            final String invalidToday = "2023.10.10";
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT)).when(dailyPlannerService).checkValidDate(invalidToday);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.searchDailyPlanner(user, userId, invalidToday));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_유효하지않은플래너작성자() {
            //given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(userId);

            //when
            final UserException result = assertThrows(UserException.class, () -> userPlannerService.searchDailyPlanner(user, userId, date));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 성공_권한없음() {
            //given
            doReturn(user2).when(userService).getUserById(user2.getId());
            doReturn(false).when(followService).havePermissionToSearch(user, user2);
            doReturn(null).when(plannerSettingService).getDday(user);

            //when
            final SearchDailyPlannerResponse searchDailyPlanner = userPlannerService.searchDailyPlanner(user, user2.getId(), date);

            //then
            assertThat(searchDailyPlanner).isNotNull();
            assertThat(searchDailyPlanner.getDate()).isEqualTo(date);
            assertThat(searchDailyPlanner.getPlannerAccessScope()).isEqualTo(user.getPlannerAccessScope().getScope());
            assertThat(searchDailyPlanner.getDday()).isNull();
            assertThat(searchDailyPlanner.getTodayGoal()).isNull();
            assertThat(searchDailyPlanner.getRetrospection()).isNull();
            assertThat(searchDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(searchDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(searchDailyPlanner.isLike()).isFalse();
            assertThat(searchDailyPlanner.getLikeCount()).isZero();
            assertThat(searchDailyPlanner.getStudyTimeHour()).isZero();
            assertThat(searchDailyPlanner.getStudyTimeMinute()).isZero();
            assertThat(searchDailyPlanner.getDailyTodos()).isNotNull().isEmpty();
        }

        @Test
        void 성공_권한있음_플래너없음() {
            //given
            doReturn(user2).when(userService).getUserById(user2.getId());
            doReturn(true).when(followService).havePermissionToSearch(user, user2);
            doReturn(null).when(dailyPlannerService).getDailyPlanner(user2, date);
            doReturn(null).when(plannerSettingService).getDday(user);

            //when
            final SearchDailyPlannerResponse searchDailyPlanner = userPlannerService.searchDailyPlanner(user, user2.getId(), date);

            //then
            assertThat(searchDailyPlanner).isNotNull();
            assertThat(searchDailyPlanner.getDate()).isEqualTo(date);
            assertThat(searchDailyPlanner.getPlannerAccessScope()).isEqualTo(user.getPlannerAccessScope().getScope());
            assertThat(searchDailyPlanner.getDday()).isNull();
            assertThat(searchDailyPlanner.getTodayGoal()).isNull();
            assertThat(searchDailyPlanner.getRetrospection()).isNull();
            assertThat(searchDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(searchDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(searchDailyPlanner.isLike()).isFalse();
            assertThat(searchDailyPlanner.getLikeCount()).isZero();
            assertThat(searchDailyPlanner.getStudyTimeHour()).isZero();
            assertThat(searchDailyPlanner.getStudyTimeMinute()).isZero();
            assertThat(searchDailyPlanner.getDailyTodos()).isNotNull().isEmpty();
        }

        @Test
        void 성공_권한있음_플래너있음() {
            //given
            final SearchDailyPlannerResponse searchDailyPlannerResponse = SearchDailyPlannerResponse.builder()
                    .date(date)
                    .plannerAccessScope(user.getPlannerAccessScope().getScope())
                    .like(false)
                    .likeCount(0)
                    .studyTimeHour(0)
                    .studyTimeMinute(0)
                    .dailyTodos(new ArrayList<>())
                    .build();
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(1L)
                    .dailyPlannerDay(date)
                    .user(user2)
                    .build();
            doReturn(user2).when(userService).getUserById(user2.getId());
            doReturn(true).when(followService).havePermissionToSearch(user, user2);
            doReturn(dailyPlanner).when(dailyPlannerService).getDailyPlanner(user2, date);
            doReturn(searchDailyPlannerResponse).when(dailyPlannerService).searchDailyPlanner(user, user2, date, dailyPlanner);
            doReturn(null).when(socialService).getSocialId(dailyPlanner);
            doReturn(null).when(plannerSettingService).getDday(user);

            //when
            final SearchDailyPlannerResponse searchDailyPlanner = userPlannerService.searchDailyPlanner(user, user2.getId(), date);

            //then
            assertThat(searchDailyPlanner).isNotNull();
            assertThat(searchDailyPlanner.getDate()).isEqualTo(date);
            assertThat(searchDailyPlanner.getPlannerAccessScope()).isEqualTo(user.getPlannerAccessScope().getScope());
            assertThat(searchDailyPlanner.getDday()).isNull();
            assertThat(searchDailyPlanner.getTodayGoal()).isNull();
            assertThat(searchDailyPlanner.getRetrospection()).isNull();
            assertThat(searchDailyPlanner.getRetrospectionImage()).isNull();
            assertThat(searchDailyPlanner.getTomorrowGoal()).isNull();
            assertThat(searchDailyPlanner.isLike()).isFalse();
            assertThat(searchDailyPlanner.getLikeCount()).isZero();
            assertThat(searchDailyPlanner.getStudyTimeHour()).isZero();
            assertThat(searchDailyPlanner.getStudyTimeMinute()).isZero();
            assertThat(searchDailyPlanner.getDailyTodos()).isNotNull().isEmpty();
        }
    }

    @Nested
    class 캘린더조회 {
        final long plannerWriterId = user2.getId();
        final String dateStr = "2023-09-01";

        @Test
        void 실패_유효하지않은플래너작성자() {
            //given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(plannerWriterId);

            //when
            final UserException result = assertThrows(UserException.class, () -> userPlannerService.searchCalendar(user, plannerWriterId, dateStr));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 실패_유효하지않은날짜형식() {
            //given
            final String invalidDay = "2023.10.01";
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT)).when(monthlyPlannerService).checkValidDate(invalidDay);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.searchCalendar(user, plannerWriterId, invalidDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_유효하지않은날짜_1일이아님() {
            //given
            final String invalidDay = "2023-10-02";
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT)).when(monthlyPlannerService).checkValidDate(invalidDay);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.searchCalendar(user, plannerWriterId, invalidDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 성공_권한없음() {
            //given
            doReturn(user2).when(userService).getUserById(plannerWriterId);
            doReturn(false).when(followService).havePermissionToSearch(user, user2);
            doReturn(0L).when(dailyPlannerService).getMonthlyLikeCount(any());

            //when
            final SearchCalendarResponse searchCalendar = userPlannerService.searchCalendar(user, plannerWriterId, dateStr);

            //then
            assertThat(searchCalendar).isNotNull();
            assertThat(searchCalendar.getPlannerAccessScope()).isEqualTo(user2.getPlannerAccessScope().getScope());
            assertThat(searchCalendar.getDayList()).isEmpty();
            assertThat(searchCalendar.getPlannerLikeCount()).isZero();
        }

        @Test
        void 성공_권한있음() {
            //given
            final LocalDate date = LocalDate.of(2023, 9, 1);
            final int lastDay = YearMonth.from(date).lengthOfMonth();
            doReturn(user2).when(userService).getUserById(plannerWriterId);
            doReturn(true).when(followService).havePermissionToSearch(user, user2);
            for (int i = 0; i < lastDay; i++) {
                final String targetDate = String.valueOf(date.plusDays(i));
                final DailyPlanner dailyPlanner = DailyPlanner.builder()
                        .id(2L)
                        .dailyPlannerDay(targetDate)
                        .user(user2)
                        .build();
                doReturn(dailyPlanner).when(dailyPlannerService).getDailyPlanner(user2, targetDate);
                doReturn(2).when(routineService).countRoutineTodo(user2, targetDate);
                doReturn(10).when(dailyPlannerService).countTodo(dailyPlanner);
            }
            doReturn(8).when(dailyPlannerService).countTodoComplete(any());
            doReturn(0L).when(dailyPlannerService).getMonthlyLikeCount(any());

            //when
            final SearchCalendarResponse searchCalendar = userPlannerService.searchCalendar(user, plannerWriterId, dateStr);

            //then
            assertThat(searchCalendar).isNotNull();
            assertThat(searchCalendar.getPlannerAccessScope()).isEqualTo(user2.getPlannerAccessScope().getScope());
            assertThat(searchCalendar.getDayList()).hasSize(lastDay);
            assertThat(searchCalendar.getPlannerLikeCount()).isZero();
        }

    }

    @Nested
    class 주간플래너조회 {
        final long plannerWriterId = 2L;
        final String startDay = "2023-10-09";
        final String endDay = "2023-10-15";

        @Test
        void 실패_유효하지않은날짜형식() {
            //given
            final String invalidStartDay = "2023.10.09";
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE_FORMAT)).when(dailyPlannerService).checkValidDate(invalidStartDay);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.searchWeeklyPlanner(user, plannerWriterId, invalidStartDay, endDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_올바르지않은날짜() {
            //given
            final String invalidStartDay = "2023-10-10";
            doThrow(new PlannerException(PlannerErrorResult.INVALID_DATE)).when(weeklyPlannerService).checkValidWeek(invalidStartDay, endDay);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> userPlannerService.searchWeeklyPlanner(user, plannerWriterId, invalidStartDay, endDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_유효하지않은플래너작성자() {
            //given
            doThrow(new UserException(UserErrorResult.NOT_FOUND_USER)).when(userService).getUserById(plannerWriterId);

            //when
            final UserException result = assertThrows(UserException.class, () -> userPlannerService.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(UserErrorResult.NOT_FOUND_USER);
        }

        @Test
        void 성공() {
            //given
            doReturn(user2).when(userService).getUserById(plannerWriterId);
            doReturn(true).when(followService).havePermissionToSearch(user, user2);
            doReturn(null).when(plannerSettingService).getDday(user);
            doReturn(new ArrayList<>()).when(weeklyPlannerService).getWeeklyTodos(user2, startDay, endDay, true);
            doReturn(WeeklyPlannerDailyResponse.builder()
                    .date(date)
                    .retrospection(null)
                    .dailyTodos(new ArrayList<>())
                    .build()).when(dailyPlannerService).getDay(user2, startDay);

            //when
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = userPlannerService.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

            //then
            assertThat(searchWeeklyPlanner).isNotNull();
            assertThat(searchWeeklyPlanner.getDday()).isNull();
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).isNotNull().isEmpty();
            assertThat(searchWeeklyPlanner.getDayList()).isNotNull().hasSize(7);
        }

    }
}
