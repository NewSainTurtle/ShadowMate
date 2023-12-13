package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.common.DateCommonService;
import com.newsainturtle.shadowmate.planner.dto.request.AddWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.RemoveWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoContentRequest;
import com.newsainturtle.shadowmate.planner.dto.request.UpdateWeeklyTodoStatusRequest;
import com.newsainturtle.shadowmate.planner.dto.response.AddWeeklyTodoResponse;
import com.newsainturtle.shadowmate.planner.entity.Weekly;
import com.newsainturtle.shadowmate.planner.entity.WeeklyTodo;
import com.newsainturtle.shadowmate.planner.exception.PlannerErrorResult;
import com.newsainturtle.shadowmate.planner.exception.PlannerException;
import com.newsainturtle.shadowmate.planner.repository.WeeklyRepository;
import com.newsainturtle.shadowmate.planner.repository.WeeklyTodoRepository;
import com.newsainturtle.shadowmate.planner.service.WeeklyPlannerServiceImpl;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklyPlannerServiceTest extends DateCommonService {

    @InjectMocks
    private WeeklyPlannerServiceImpl weeklyPlannerServiceImpl;

    @Mock
    private WeeklyRepository weeklyRepository;

    @Mock
    private WeeklyTodoRepository weeklyTodoRepository;

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
                    .startDay(startDay)
                    .endDay(endDay)
                    .user(user)
                    .build();

            final WeeklyTodo weeklyTodo = WeeklyTodo.builder()
                    .id(1L)
                    .weekly(weekly)
                    .weeklyTodoContent("자기소개서 제출하기")
                    .weeklyTodoStatus(false)
                    .build();

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).save(any(WeeklyTodo.class));

            //when
            final AddWeeklyTodoResponse addWeeklyTodoResponse = weeklyPlannerServiceImpl.addWeeklyTodo(user, addWeeklyTodoRequest);

            //then
            assertThat(addWeeklyTodoResponse.getWeeklyTodoId()).isNotNull();

            //verify
            verify(weeklyRepository, times(1)).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));
            verify(weeklyTodoRepository, times(1)).save(any(WeeklyTodo.class));
        }

    }

    @Nested
    class 주차별할일내용수정 {
        final String changeWeeklyTodoContent = "자기소개서 첨삭하기";
        final Weekly weekly = Weekly.builder()
                .id(1L)
                .startDay(startDay)
                .endDay(endDay)
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

            doReturn(null).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, updateWeeklyTodoContentRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_WEEKLY_PLANNER);
        }

        @Test
        void 실패_유효하지않은위클리할일() {
            //given

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));
            doReturn(null).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, updateWeeklyTodoContentRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
        }

        @Test
        void 성공() {
            //given

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));

            //when
            weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, updateWeeklyTodoContentRequest);

            //then

            //verify
            verify(weeklyRepository, times(1)).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));
            verify(weeklyTodoRepository, times(1)).findByIdAndWeekly(any(Long.class), any(Weekly.class));
        }

    }

    @Nested
    class 주차별할일상태수정 {
        final Weekly weekly = Weekly.builder()
                .id(1L)
                .startDay(startDay)
                .endDay(endDay)
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

            doReturn(null).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoStatus(user, updateWeeklyTodoStatusRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_WEEKLY_PLANNER);
        }

        @Test
        void 실패_유효하지않은위클리할일() {
            //given

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));
            doReturn(null).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoStatus(user, updateWeeklyTodoStatusRequest));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
        }

        @Test
        void 성공() {
            //given

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));

            //when
            weeklyPlannerServiceImpl.updateWeeklyTodoStatus(user, updateWeeklyTodoStatusRequest);

            //then

            //verify
            verify(weeklyRepository, times(1)).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));
            verify(weeklyTodoRepository, times(1)).findByIdAndWeekly(any(Long.class), any(Weekly.class));
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
            doReturn(null).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));

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
                    .startDay(startDay)
                    .endDay(endDay)
                    .user(user)
                    .build();

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));

            //when
            weeklyPlannerServiceImpl.removeWeeklyTodo(user, removeWeeklyTodoRequest);

            //then

            //verify
            verify(weeklyRepository, times(1)).findByUserAndStartDayAndEndDay(any(), any(String.class), any(String.class));
            verify(weeklyTodoRepository, times(1)).deleteByIdAndWeekly(any(Long.class), any(Weekly.class));
        }

    }
}
