package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.planner.dto.response.SearchCalendarResponse;
import com.newsainturtle.shadowmate.planner.dto.response.SearchDailyPlannerResponse;
import com.newsainturtle.shadowmate.planner.dto.response.SearchWeeklyPlannerResponse;
import com.newsainturtle.shadowmate.planner.entity.*;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.*;
import com.newsainturtle.shadowmate.planner.service.SearchPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import com.newsainturtle.shadowmate.planner_setting.entity.CategoryColor;
import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class SearchPlannerServiceTest {

    @InjectMocks
    private SearchPlannerServiceImpl searchPlannerServiceImpl;

    @Mock
    private WeeklyRepository weeklyRepository;

    @Mock
    private WeeklyTodoRepository weeklyTodoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DailyPlannerRepository dailyPlannerRepository;

    @Mock
    private DailyPlannerLikeRepository dailyPlannerLikeRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private DdayRepository ddayRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private SocialRepository socialRepository;

    private final String date = "2023-09-25";
    private final String startDay = "2023-10-09";
    private final String endDay = "2023-10-15";
    private final String weeklyTodoContent = "자기소개서 제출하기";
    private final String todoContent = "수능완성 수학 과목별 10문제";

    private final String email = "yntest@shadowmate.com";
    private final String password = "yntest1234";
    private final String nickname = "거북이";
    private final SocialType socialType = SocialType.BASIC;
    private final PlannerAccessScope plannerAccessScope = PlannerAccessScope.PUBLIC;

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
            .dailyPlannerDay(Date.valueOf(date))
            .user(user)
            .build();

    private LocalDateTime stringToLocalDateTime(String timeStr) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(timeStr, formatter);
    }

    @Nested
    class 일일플래너조회 {
        final long userId = 1L;
        final long plannerWriterId = 2L;
        final String today = "2023-10-10";

        @Test
        void 실패_유효하지않은날짜형식() {
            //given
            final String invalidToday = "2023.10.10";

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchDailyPlanner(user, userId, invalidToday));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_유효하지않은플래너작성자() {
            //given
            doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(userId);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchDailyPlanner(user, userId, today));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
        }

        @Test
        void 성공_플래너없을때() {
            //given
            doReturn(user).when(userRepository).findByIdAndWithdrawalIsFalse(userId);
            doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(any(), any(Date.class));

            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = searchPlannerServiceImpl.searchDailyPlanner(user, userId, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(plannerAccessScope.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isNull();
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospection()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isNull();
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isNull();
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isZero();
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isZero();
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isZero();
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNull();
        }

        @Test
        void 성공_플래너있을때_비공개() {
            //given
            final User plannerWriter = User.builder()
                    .id(plannerWriterId)
                    .email("jntest@shadowmate.com")
                    .password(password)
                    .socialLogin(socialType)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.PRIVATE)
                    .withdrawal(false)
                    .build();
            final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                    .id(plannerWriterId)
                    .dailyPlannerDay(Date.valueOf(date))
                    .user(plannerWriter)
                    .build();

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(dailyPlanner2).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(any(), any(Date.class));

            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = searchPlannerServiceImpl.searchDailyPlanner(user, plannerWriterId, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.PRIVATE.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isNull();
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospection()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isNull();
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isNull();
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isZero();
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isZero();
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isZero();
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNull();
        }

        @Test
        void 성공_플래너있을때_친구공개_친구아님() {
            //given
            final User plannerWriter = User.builder()
                    .id(plannerWriterId)
                    .email("jntest@shadowmate.com")
                    .password(password)
                    .socialLogin(socialType)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();
            final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                    .id(plannerWriterId)
                    .dailyPlannerDay(Date.valueOf(date))
                    .user(plannerWriter)
                    .build();

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(dailyPlanner2).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(null).when(followRepository).findByFollowerIdAndFollowingId(any(), any());
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(any(), any(Date.class));

            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = searchPlannerServiceImpl.searchDailyPlanner(user, plannerWriterId, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.FOLLOW.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isNull();
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospection()).isNull();
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isNull();
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isNull();
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isZero();
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isZero();
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isZero();
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNull();
        }

        @Test
        void 성공_플래너있을때_친구공개_친구() {
            //given
            final User plannerWriter = User.builder()
                    .id(plannerWriterId)
                    .email("jntest@shadowmate.com")
                    .password(password)
                    .socialLogin(socialType)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();
            final Follow follow = Follow.builder()
                    .id(1L)
                    .followerId(user)
                    .followingId(plannerWriter)
                    .build();
            final DailyPlanner dailyPlanner2 = DailyPlanner.builder()
                    .id(plannerWriterId)
                    .dailyPlannerDay(Date.valueOf(today))
                    .user(plannerWriter)
                    .build();
            final Date birthday = Date.valueOf(LocalDate.now());
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(birthday)
                    .user(user)
                    .build();
            final CategoryColor categoryColor = CategoryColor.builder()
                    .categoryColorCode("#D9B5D9")
                    .build();
            final Category category = Category.builder()
                    .id(1L)
                    .categoryColor(categoryColor)
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build();
            final List<Todo> todoList = new ArrayList<>();
            todoList.add(Todo.builder()
                    .id(1L)
                    .category(category)
                    .todoContent(todoContent)
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .timeTable(TimeTable.builder()
                            .startTime(stringToLocalDateTime("2023-10-10 22:50"))
                            .endTime(stringToLocalDateTime("2023-10-11 01:30"))
                            .build())
                    .build());

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(dailyPlanner2).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(follow).when(followRepository).findByFollowerIdAndFollowingId(any(), any());
            doReturn(dday).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(socialRepository).findByDailyPlannerAndDeleteTimeIsNull(any(DailyPlanner.class));
            doReturn(null).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));
            doReturn(127L).when(dailyPlannerLikeRepository).countByDailyPlanner(any(DailyPlanner.class));
            doReturn(todoList).when(todoRepository).findAllByDailyPlanner(any(DailyPlanner.class));

            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = searchPlannerServiceImpl.searchDailyPlanner(user, plannerWriterId, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.FOLLOW.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isEqualTo(birthday.toString());
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isEqualTo(dailyPlanner.getTodayGoal());
            assertThat(searchDailyPlannerResponse.getRetrospection()).isEqualTo(dailyPlanner.getRetrospection());
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isEqualTo(dailyPlanner.getRetrospectionImage());
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isEqualTo(dailyPlanner.getTomorrowGoal());
            assertThat(searchDailyPlannerResponse.isShareSocial()).isFalse();
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isEqualTo(127L);
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isEqualTo(2);
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isEqualTo(40);
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNotNull();
            assertThat(searchDailyPlannerResponse.getDailyTodos()).hasSize(1);
        }

        @Test
        void 성공_플래너있을때_전체공개() {
            //given
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(userId)
                    .dailyPlannerDay(Date.valueOf(today))
                    .user(user)
                    .build();
            final Date birthday = Date.valueOf(LocalDate.now());
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(birthday)
                    .user(user)
                    .build();
            final CategoryColor categoryColor = CategoryColor.builder()
                    .categoryColorCode("#D9B5D9")
                    .build();
            final Category category = Category.builder()
                    .id(1L)
                    .categoryColor(categoryColor)
                    .user(user)
                    .categoryTitle("국어")
                    .categoryRemove(false)
                    .categoryEmoticon("🍅")
                    .build();
            final List<Todo> todoList = new ArrayList<>();
            todoList.add(Todo.builder()
                    .id(1L)
                    .category(category)
                    .todoContent(todoContent)
                    .todoStatus(TodoStatus.EMPTY)
                    .dailyPlanner(dailyPlanner)
                    .timeTable(TimeTable.builder()
                            .startTime(stringToLocalDateTime("2023-10-10 22:50"))
                            .endTime(stringToLocalDateTime("2023-10-11 01:30"))
                            .build())
                    .build());

            doReturn(user).when(userRepository).findByIdAndWithdrawalIsFalse(userId);
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(dday).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(socialRepository).findByDailyPlannerAndDeleteTimeIsNull(any(DailyPlanner.class));
            doReturn(null).when(dailyPlannerLikeRepository).findByUserAndDailyPlanner(any(), any(DailyPlanner.class));
            doReturn(127L).when(dailyPlannerLikeRepository).countByDailyPlanner(any(DailyPlanner.class));
            doReturn(todoList).when(todoRepository).findAllByDailyPlanner(any(DailyPlanner.class));


            //when
            final SearchDailyPlannerResponse searchDailyPlannerResponse = searchPlannerServiceImpl.searchDailyPlanner(user, userId, today);

            //then
            assertThat(searchDailyPlannerResponse).isNotNull();
            assertThat(searchDailyPlannerResponse.getDate()).isEqualTo(today);
            assertThat(searchDailyPlannerResponse.getPlannerAccessScope()).isEqualTo(plannerAccessScope.getScope());
            assertThat(searchDailyPlannerResponse.getDday()).isEqualTo(birthday.toString());
            assertThat(searchDailyPlannerResponse.getTodayGoal()).isEqualTo(dailyPlanner.getTodayGoal());
            assertThat(searchDailyPlannerResponse.getRetrospection()).isEqualTo(dailyPlanner.getRetrospection());
            assertThat(searchDailyPlannerResponse.getRetrospectionImage()).isEqualTo(dailyPlanner.getRetrospectionImage());
            assertThat(searchDailyPlannerResponse.getTomorrowGoal()).isEqualTo(dailyPlanner.getTomorrowGoal());
            assertThat(searchDailyPlannerResponse.isShareSocial()).isFalse();
            assertThat(searchDailyPlannerResponse.isLike()).isFalse();
            assertThat(searchDailyPlannerResponse.getLikeCount()).isEqualTo(127L);
            assertThat(searchDailyPlannerResponse.getStudyTimeHour()).isEqualTo(2);
            assertThat(searchDailyPlannerResponse.getStudyTimeMinute()).isEqualTo(40);
            assertThat(searchDailyPlannerResponse.getDailyTodos()).isNotNull();
            assertThat(searchDailyPlannerResponse.getDailyTodos()).hasSize(1);
        }
    }

    @Nested
    class 캘린더조회 {
        final long plannerWriterId = 2L;
        final String dateStr = "2023-10-01";
        final int lastDay = 31;

        final User plannerWriter = User.builder()
                .id(plannerWriterId)
                .email("jntest@shadowmate.com")
                .password(password)
                .socialLogin(socialType)
                .nickname("토끼")
                .plannerAccessScope(plannerAccessScope)
                .withdrawal(false)
                .build();
        final DailyPlanner dailyPlanner = DailyPlanner.builder()
                .id(2L)
                .dailyPlannerDay(Date.valueOf("2023-10-01"))
                .user(plannerWriter)
                .build();

        @Test
        void 실패_유효하지않은날짜형식() {
            //given
            final String invalidDay = "2023.10.01";

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, invalidDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_유효하지않은날짜_1일이아님() {
            //given
            final String invalidDay = "2023-10-02";

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, invalidDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_유효하지않은플래너작성자() {
            //given
            doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, dateStr));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
        }

        @Test
        void 성공_비공개() {
            //given
            final User plannerWriter = User.builder()
                    .id(plannerWriterId)
                    .email("jntest@shadowmate.com")
                    .password(password)
                    .socialLogin(socialType)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.PRIVATE)
                    .withdrawal(false)
                    .build();

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final SearchCalendarResponse searchCalendarResponse = searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, dateStr);

            //then
            assertThat(searchCalendarResponse).isNotNull();
            assertThat(searchCalendarResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.PRIVATE.getScope());
            assertThat(searchCalendarResponse.getDayList()).isEmpty();
        }

        @Test
        void 성공_친구공개_친구아님() {
            //given
            final User plannerWriter = User.builder()
                    .id(plannerWriterId)
                    .email("jntest@shadowmate.com")
                    .password(password)
                    .socialLogin(socialType)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final SearchCalendarResponse searchCalendarResponse = searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, dateStr);

            //then
            assertThat(searchCalendarResponse).isNotNull();
            assertThat(searchCalendarResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.FOLLOW.getScope());
            assertThat(searchCalendarResponse.getDayList()).isEmpty();
        }

        @Test
        void 성공_친구공개_친구() {
            //given
            final User plannerWriter = User.builder()
                    .id(plannerWriterId)
                    .email("jntest@shadowmate.com")
                    .password(password)
                    .socialLogin(socialType)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();
            final Follow follow = Follow.builder()
                    .id(1L)
                    .followerId(user)
                    .followingId(plannerWriter)
                    .build();
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(2L)
                    .dailyPlannerDay(Date.valueOf("2023-10-01"))
                    .user(plannerWriter)
                    .build();

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(follow).when(followRepository).findByFollowerIdAndFollowingId(any(), any());
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(0).when(todoRepository).countByDailyPlanner(any(DailyPlanner.class));

            //when
            final SearchCalendarResponse searchCalendarResponse = searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, dateStr);

            //then
            assertThat(searchCalendarResponse).isNotNull();
            assertThat(searchCalendarResponse.getPlannerAccessScope()).isEqualTo(PlannerAccessScope.FOLLOW.getScope());
            assertThat(searchCalendarResponse.getDayList()).hasSize(lastDay);
        }

        @Test
        void 성공_전체공개_할일등록안함() {
            //given
            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(0).when(todoRepository).countByDailyPlanner(any(DailyPlanner.class));

            //when
            final SearchCalendarResponse searchCalendarResponse = searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, dateStr);

            //then
            assertThat(searchCalendarResponse).isNotNull();
            assertThat(searchCalendarResponse.getPlannerAccessScope()).isEqualTo(plannerAccessScope.getScope());
            assertThat(searchCalendarResponse.getDayList()).hasSize(lastDay);
            assertThat(searchCalendarResponse.getDayList().get(0).getDate()).isEqualTo(dateStr);
            assertThat(searchCalendarResponse.getDayList().get(lastDay - 1).getDate()).isEqualTo("2023-10-31");
            assertThat(searchCalendarResponse.getDayList().get(0).getDayStatus()).isZero();
            assertThat(searchCalendarResponse.getDayList().get(0).getTodoCount()).isZero();
        }

        @Test
        void 성공_전체공개_할일60미만달성() {
            //given
            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(10).when(todoRepository).countByDailyPlanner(any(DailyPlanner.class));
            doReturn(6).when(todoRepository).countByDailyPlannerAndTodoStatusNot(any(DailyPlanner.class), any(TodoStatus.class));

            //when
            final SearchCalendarResponse searchCalendarResponse = searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, dateStr);

            //then
            assertThat(searchCalendarResponse).isNotNull();
            assertThat(searchCalendarResponse.getPlannerAccessScope()).isEqualTo(plannerAccessScope.getScope());
            assertThat(searchCalendarResponse.getDayList()).hasSize(lastDay);
            assertThat(searchCalendarResponse.getDayList().get(0).getDate()).isEqualTo(dateStr);
            assertThat(searchCalendarResponse.getDayList().get(lastDay - 1).getDate()).isEqualTo("2023-10-31");
            assertThat(searchCalendarResponse.getDayList().get(0).getDayStatus()).isEqualTo(1);
            assertThat(searchCalendarResponse.getDayList().get(0).getTodoCount()).isEqualTo(6);
        }

        @Test
        void 성공_전체공개_할일100미만60이상달성() {
            //given
            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(10).when(todoRepository).countByDailyPlanner(any(DailyPlanner.class));
            doReturn(2).when(todoRepository).countByDailyPlannerAndTodoStatusNot(any(DailyPlanner.class), any(TodoStatus.class));

            //when
            final SearchCalendarResponse searchCalendarResponse = searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, dateStr);

            //then
            assertThat(searchCalendarResponse).isNotNull();
            assertThat(searchCalendarResponse.getPlannerAccessScope()).isEqualTo(plannerAccessScope.getScope());
            assertThat(searchCalendarResponse.getDayList()).hasSize(lastDay);
            assertThat(searchCalendarResponse.getDayList().get(0).getDate()).isEqualTo(dateStr);
            assertThat(searchCalendarResponse.getDayList().get(lastDay - 1).getDate()).isEqualTo("2023-10-31");
            assertThat(searchCalendarResponse.getDayList().get(0).getDayStatus()).isEqualTo(2);
            assertThat(searchCalendarResponse.getDayList().get(0).getTodoCount()).isEqualTo(2);
        }

        @Test
        void 성공_전체공개_할일100달성() {
            //given
            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(2).when(todoRepository).countByDailyPlanner(any(DailyPlanner.class));
            doReturn(0).when(todoRepository).countByDailyPlannerAndTodoStatusNot(any(DailyPlanner.class), any(TodoStatus.class));

            //when
            final SearchCalendarResponse searchCalendarResponse = searchPlannerServiceImpl.searchCalendar(user, plannerWriterId, dateStr);

            //then
            assertThat(searchCalendarResponse).isNotNull();
            assertThat(searchCalendarResponse.getPlannerAccessScope()).isEqualTo(plannerAccessScope.getScope());
            assertThat(searchCalendarResponse.getDayList()).hasSize(lastDay);
            assertThat(searchCalendarResponse.getDayList().get(0).getDate()).isEqualTo(dateStr);
            assertThat(searchCalendarResponse.getDayList().get(lastDay - 1).getDate()).isEqualTo("2023-10-31");
            assertThat(searchCalendarResponse.getDayList().get(0).getDayStatus()).isEqualTo(3);
            assertThat(searchCalendarResponse.getDayList().get(0).getTodoCount()).isZero();
        }


    }

    @Nested
    class 주간플래너조회 {
        final long plannerWriterId = 2L;
        final User plannerWriter = User.builder()
                .id(2L)
                .email("jntest@shadowmate.com")
                .password(password)
                .socialLogin(socialType)
                .nickname("토끼")
                .plannerAccessScope(plannerAccessScope)
                .withdrawal(false)
                .build();
        final Weekly weekly = Weekly.builder()
                .id(1L)
                .startDay(Date.valueOf(startDay))
                .endDay(Date.valueOf(endDay))
                .user(plannerWriter)
                .build();
        final WeeklyTodo weeklyTodo = WeeklyTodo.builder()
                .id(1L)
                .weekly(weekly)
                .weeklyTodoContent(weeklyTodoContent)
                .weeklyTodoStatus(false)
                .build();

        @Test
        void 실패_유효하지않은날짜형식_시작날짜() {
            //given
            final String invalidStartDay = "2023.10.09";

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, invalidStartDay, endDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_유효하지않은날짜형식_끝날짜() {
            //given
            final String invalidEndDay = "2023.10.15";

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, invalidEndDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_유효하지않은플래너작성자() {
            //given
            doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
        }

        @Test
        void 실패_올바르지않은날짜_시작요일이월요일이아님() {
            //given
            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, "2023-10-10", endDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_올바르지않은날짜_일주일간격이아님() {
            //given
            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, "2023-10-14"));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 성공_플래너없을때() {
            //given
            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(new ArrayList<>()).when(weeklyTodoRepository).findAllByWeekly(any(Weekly.class));
            doReturn(null).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(any(), any(Date.class));

            //when
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

            //then
            assertThat(searchWeeklyPlanner).isNotNull();
            assertThat(searchWeeklyPlanner.getDday()).isNull();
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).isNotNull();
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).isEmpty();
            assertThat(searchWeeklyPlanner.getDayList()).isNotNull();
            assertThat(searchWeeklyPlanner.getDayList()).hasSize(7);
        }

        @Test
        void 성공_플래너있을때_비공개() {
            //given
            final User plannerWriter = User.builder()
                    .id(2L)
                    .email("jntest@shadowmate.com")
                    .password(password)
                    .socialLogin(socialType)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.PRIVATE)
                    .withdrawal(false)
                    .build();
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(2L)
                    .dailyPlannerDay(Date.valueOf(startDay))
                    .user(plannerWriter)
                    .build();

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(new ArrayList<>()).when(weeklyTodoRepository).findAllByWeekly(any(Weekly.class));
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(any(), any(Date.class));

            //when
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

            //then
            assertThat(searchWeeklyPlanner).isNotNull();
            assertThat(searchWeeklyPlanner.getDday()).isNull();
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).isNotNull();
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).isEmpty();
            assertThat(searchWeeklyPlanner.getDayList()).isNotNull();
            assertThat(searchWeeklyPlanner.getDayList()).hasSize(7);
        }

        @Test
        void 성공_플래너있을때_친구공개_친구아님() {
            //given
            final User plannerWriter = User.builder()
                    .id(2L)
                    .email("jntest@shadowmate.com")
                    .password(password)
                    .socialLogin(socialType)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(2L)
                    .dailyPlannerDay(Date.valueOf(startDay))
                    .user(plannerWriter)
                    .build();

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(null).when(followRepository).findByFollowerIdAndFollowingId(any(), any());
            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(new ArrayList<>()).when(weeklyTodoRepository).findAllByWeekly(any(Weekly.class));
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));
            doReturn(null).when(ddayRepository).findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(any(), any(Date.class));

            //when
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

            //then
            assertThat(searchWeeklyPlanner).isNotNull();
            assertThat(searchWeeklyPlanner.getDday()).isNull();
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).isNotNull();
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).isEmpty();
            assertThat(searchWeeklyPlanner.getDayList()).isNotNull();
            assertThat(searchWeeklyPlanner.getDayList()).hasSize(7);
        }

        @Test
        void 성공_플래너있을때_친구공개_친구() {
            //given
            final User plannerWriter = User.builder()
                    .id(2L)
                    .email("jntest@shadowmate.com")
                    .password(password)
                    .socialLogin(socialType)
                    .nickname("토끼")
                    .plannerAccessScope(PlannerAccessScope.FOLLOW)
                    .withdrawal(false)
                    .build();
            final Follow follow = Follow.builder()
                    .id(1L)
                    .followerId(user)
                    .followingId(plannerWriter)
                    .build();
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(2L)
                    .dailyPlannerDay(Date.valueOf(startDay))
                    .user(plannerWriter)
                    .build();
            final Date birthday = Date.valueOf(LocalDate.now());
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(birthday)
                    .user(user)
                    .build();
            final List<WeeklyTodo> weeklyTodoList = new ArrayList<>();
            weeklyTodoList.add(WeeklyTodo.builder()
                    .id(1L)
                    .weekly(weekly)
                    .weeklyTodoContent(weeklyTodoContent)
                    .weeklyTodoStatus(false)
                    .build());

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(follow).when(followRepository).findByFollowerIdAndFollowingId(any(), any());
            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(weeklyTodoList).when(weeklyTodoRepository).findAllByWeekly(any(Weekly.class));
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(new ArrayList<>()).when(todoRepository).findAllByDailyPlanner(any(DailyPlanner.class));
            doReturn(dday).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));

            //when
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

            //then
            assertThat(searchWeeklyPlanner).isNotNull();
            assertThat(searchWeeklyPlanner.getDday()).isEqualTo(birthday.toString());
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).isNotNull();
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).hasSize(1);
            assertThat(searchWeeklyPlanner.getDayList()).isNotNull();
            assertThat(searchWeeklyPlanner.getDayList()).hasSize(7);
        }

        @Test
        void 성공_플래너있을때_전체공개() {
            //given
            final DailyPlanner dailyPlanner = DailyPlanner.builder()
                    .id(2L)
                    .dailyPlannerDay(Date.valueOf(startDay))
                    .user(plannerWriter)
                    .build();
            final Date birthday = Date.valueOf(LocalDate.now());
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(birthday)
                    .user(user)
                    .build();
            final List<WeeklyTodo> weeklyTodoList = new ArrayList<>();
            weeklyTodoList.add(WeeklyTodo.builder()
                    .id(1L)
                    .weekly(weekly)
                    .weeklyTodoContent(weeklyTodoContent)
                    .weeklyTodoStatus(false)
                    .build());

            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);
            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(weeklyTodoList).when(weeklyTodoRepository).findAllByWeekly(any(Weekly.class));
            doReturn(dailyPlanner).when(dailyPlannerRepository).findByUserAndDailyPlannerDay(any(), any(Date.class));
            doReturn(new ArrayList<>()).when(todoRepository).findAllByDailyPlanner(any(DailyPlanner.class));
            doReturn(dday).when(ddayRepository).findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(any(), any(Date.class));

            //when
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = searchPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

            //then
            assertThat(searchWeeklyPlanner).isNotNull();
            assertThat(searchWeeklyPlanner.getDday()).isEqualTo(birthday.toString());
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).isNotNull();
            assertThat(searchWeeklyPlanner.getWeeklyTodos()).hasSize(1);
            assertThat(searchWeeklyPlanner.getDayList()).isNotNull();
            assertThat(searchWeeklyPlanner.getDayList()).hasSize(7);
        }

    }
}
