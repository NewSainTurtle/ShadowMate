package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.follow.entity.Follow;
import com.newsainturtle.shadowmate.follow.repository.FollowRepository;
import com.newsainturtle.shadowmate.planner.dto.request.AddWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoContentRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoStatusRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddWeeklyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.response.SearchWeeklyPlannerResponse;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.Weekly;
import com.newsainturtle.shadowmate.planner.entity.WeeklyTodo;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
import com.newsainturtle.shadowmate.planner.repository.WeeklyRepository;
import com.newsainturtle.shadowmate.planner.repository.WeeklyTodoRepository;
import com.newsainturtle.shadowmate.planner.service.WeeklyPlannerServiceImpl;
import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklyPlannerServiceTest {

    @InjectMocks
    private WeeklyPlannerServiceImpl weeklyPlannerServiceImpl;

    @Mock
    private WeeklyRepository weeklyRepository;

    @Mock
    private WeeklyTodoRepository weeklyTodoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DailyPlannerRepository dailyPlannerRepository;

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private DdayRepository ddayRepository;

    @Mock
    private FollowRepository followRepository;

    private final String startDay = "2023-10-09";
    private final String endDay = "2023-10-15";
    private final String weeklyTodoContent = "자기소개서 제출하기";

    private final String password = "yntest1234";
    private final SocialType socialType = SocialType.BASIC;
    private final PlannerAccessScope plannerAccessScope = PlannerAccessScope.PUBLIC;

    private final User user = User.builder()
            .id(1L)
            .email("yntest@shadowmate.com")
            .password(password)
            .socialLogin(socialType)
            .nickname("거북이")
            .plannerAccessScope(plannerAccessScope)
            .withdrawal(false)
            .build();

    @Nested
    class 주차별할일등록 {

        @Test
        void 실패_올바르지않은날짜_시작요일이월요일이아님() {
            //given
            final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                    .startDate("2023-10-10")
                    .endDate(endDay)
                    .weeklyTodoContent(weeklyTodoContent)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.addWeeklyTodo(user, addWeeklyTodoRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_올바르지않은날짜_일주일간격이아님() {
            //given
            final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                    .startDate(startDay)
                    .endDate("2023-10-16")
                    .weeklyTodoContent(weeklyTodoContent)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.addWeeklyTodo(user, addWeeklyTodoRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 성공() {
            //given
            final AddWeeklyTodoRequest addWeeklyTodoRequest = AddWeeklyTodoRequest.builder()
                    .startDate(startDay)
                    .endDate(endDay)
                    .weeklyTodoContent(weeklyTodoContent)
                    .build();
            final Weekly weekly = Weekly.builder()
                    .id(1L)
                    .startDay(Date.valueOf(startDay))
                    .endDay(Date.valueOf(endDay))
                    .user(user)
                    .build();

            final WeeklyTodo weeklyTodo = WeeklyTodo.builder()
                    .id(1L)
                    .weekly(weekly)
                    .weeklyTodoContent("자기소개서 제출하기")
                    .weeklyTodoStatus(false)
                    .build();

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).save(any(WeeklyTodo.class));

            //when
            final AddWeeklyTodoResponse addWeeklyTodoResponse = weeklyPlannerServiceImpl.addWeeklyTodo(user, addWeeklyTodoRequest);

            //then
            assertThat(addWeeklyTodoResponse.getWeeklyTodoId()).isNotNull();

            //verify
            verify(weeklyRepository, times(1)).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            verify(weeklyTodoRepository, times(1)).save(any(WeeklyTodo.class));
        }

    }

    @Nested
    class 주차별할일내용수정 {
        final String changeWeeklyTodoContent = "자기소개서 첨삭하기";
        final Weekly weekly = Weekly.builder()
                .id(1L)
                .startDay(Date.valueOf(startDay))
                .endDay(Date.valueOf(endDay))
                .user(user)
                .build();
        final WeeklyTodo weeklyTodo = WeeklyTodo.builder()
                .id(1L)
                .weekly(weekly)
                .weeklyTodoContent(weeklyTodoContent)
                .weeklyTodoStatus(false)
                .build();
        final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                .startDate(startDay)
                .endDate(endDay)
                .weeklyTodoId(weeklyTodo.getId())
                .weeklyTodoContent(changeWeeklyTodoContent)
                .build();

        @Test
        void 실패_올바르지않은날짜_시작요일이월요일이아님() {
            //given
            final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                    .startDate("2023-10-10")
                    .endDate(endDay)
                    .weeklyTodoId(weeklyTodo.getId())
                    .weeklyTodoContent(changeWeeklyTodoContent)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, updateWeeklyTodoContentRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_올바르지않은날짜_일주일간격이아님() {
            //given
            final UpdateWeeklyTodoContentRequest updateWeeklyTodoContentRequest = UpdateWeeklyTodoContentRequest.builder()
                    .startDate(startDay)
                    .endDate("2023-10-16")
                    .weeklyTodoId(weeklyTodo.getId())
                    .weeklyTodoContent(changeWeeklyTodoContent)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, updateWeeklyTodoContentRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_유효하지않은위클리() {
            //given

            doReturn(null).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, updateWeeklyTodoContentRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_WEEKLY_PLANNER);
        }

        @Test
        void 실패_유효하지않은위클리할일() {
            //given

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(null).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, updateWeeklyTodoContentRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
        }

        @Test
        void 성공() {
            //given

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).save(any(WeeklyTodo.class));

            //when
            weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, updateWeeklyTodoContentRequest);

            //then

            //verify
            verify(weeklyRepository, times(1)).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            verify(weeklyTodoRepository, times(1)).findByIdAndWeekly(any(Long.class), any(Weekly.class));
            verify(weeklyTodoRepository, times(1)).save(any(WeeklyTodo.class));
        }

    }

    @Nested
    class 주차별할일상태수정 {
        final Weekly weekly = Weekly.builder()
                .id(1L)
                .startDay(Date.valueOf(startDay))
                .endDay(Date.valueOf(endDay))
                .user(user)
                .build();
        final WeeklyTodo weeklyTodo = WeeklyTodo.builder()
                .id(1L)
                .weekly(weekly)
                .weeklyTodoContent(weeklyTodoContent)
                .weeklyTodoStatus(false)
                .build();
        final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                .startDate(startDay)
                .endDate(endDay)
                .weeklyTodoId(weeklyTodo.getId())
                .weeklyTodoStatus(true)
                .build();

        @Test
        void 실패_올바르지않은날짜_시작요일이월요일이아님() {
            //given
            final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                    .startDate("2023-10-10")
                    .endDate(endDay)
                    .weeklyTodoId(weeklyTodo.getId())
                    .weeklyTodoStatus(true)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoStatus(user, updateWeeklyTodoStatusRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_올바르지않은날짜_일주일간격이아님() {
            //given
            final UpdateWeeklyTodoStatusRequest updateWeeklyTodoStatusRequest = UpdateWeeklyTodoStatusRequest.builder()
                    .startDate(startDay)
                    .endDate("2023-10-16")
                    .weeklyTodoId(weeklyTodo.getId())
                    .weeklyTodoStatus(true)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoStatus(user, updateWeeklyTodoStatusRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_유효하지않은위클리() {
            //given

            doReturn(null).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoStatus(user, updateWeeklyTodoStatusRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_WEEKLY_PLANNER);
        }

        @Test
        void 실패_유효하지않은위클리할일() {
            //given

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(null).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoStatus(user, updateWeeklyTodoStatusRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
        }

        @Test
        void 성공() {
            //given

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).save(any(WeeklyTodo.class));

            //when
            weeklyPlannerServiceImpl.updateWeeklyTodoStatus(user, updateWeeklyTodoStatusRequest);

            //then

            //verify
            verify(weeklyRepository, times(1)).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            verify(weeklyTodoRepository, times(1)).findByIdAndWeekly(any(Long.class), any(Weekly.class));
            verify(weeklyTodoRepository, times(1)).save(any(WeeklyTodo.class));
        }

    }

    @Nested
    class 주차별할일삭제 {

        final RemoveWeeklyTodoRequest removeWeeklyTodoRequest = RemoveWeeklyTodoRequest.builder()
                .startDate(startDay)
                .endDate(endDay)
                .weeklyTodoId(1L)
                .build();

        @Test
        void 실패_올바르지않은날짜_시작요일이월요일이아님() {
            //given
            final RemoveWeeklyTodoRequest removeWeeklyTodoRequest = RemoveWeeklyTodoRequest.builder()
                    .startDate("2023-10-10")
                    .endDate(endDay)
                    .weeklyTodoId(1L)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.removeWeeklyTodo(user, removeWeeklyTodoRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_올바르지않은날짜_일주일간격이아님() {
            //given
            final RemoveWeeklyTodoRequest removeWeeklyTodoRequest = RemoveWeeklyTodoRequest.builder()
                    .startDate(startDay)
                    .endDate("2023-10-16")
                    .weeklyTodoId(1L)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.removeWeeklyTodo(user, removeWeeklyTodoRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_유효하지않은위클리() {
            //given
            doReturn(null).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.removeWeeklyTodo(user, removeWeeklyTodoRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_WEEKLY_PLANNER);
        }

        @Test
        void 성공() {
            //given
            final Weekly weekly = Weekly.builder()
                    .id(1L)
                    .startDay(Date.valueOf(startDay))
                    .endDay(Date.valueOf(endDay))
                    .user(user)
                    .build();

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));

            //when
            weeklyPlannerServiceImpl.removeWeeklyTodo(user, removeWeeklyTodoRequest);

            //then

            //verify
            verify(weeklyRepository, times(1)).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            verify(weeklyTodoRepository, times(1)).deleteByIdAndWeekly(any(Long.class), any(Weekly.class));
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
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, invalidStartDay, endDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_유효하지않은날짜형식_끝날짜() {
            //given
            final String invalidEndDay = "2023.10.15";

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, invalidEndDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE_FORMAT);
        }

        @Test
        void 실패_유효하지않은플래너작성자() {
            //given
            doReturn(null).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_USER);
        }

        @Test
        void 실패_올바르지않은날짜_시작요일이월요일이아님() {
            //given
            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, "2023-10-10", endDay));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        void 실패_올바르지않은날짜_일주일간격이아님() {
            //given
            doReturn(plannerWriter).when(userRepository).findByIdAndWithdrawalIsFalse(plannerWriterId);

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, "2023-10-14"));

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
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

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
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

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
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

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
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

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
            final SearchWeeklyPlannerResponse searchWeeklyPlanner = weeklyPlannerServiceImpl.searchWeeklyPlanner(user, plannerWriterId, startDay, endDay);

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
