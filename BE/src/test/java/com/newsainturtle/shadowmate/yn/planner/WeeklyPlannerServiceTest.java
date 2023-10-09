package com.newsainturtle.shadowmate.yn.planner;

import com.newsainturtle.shadowmate.planner.dto.AddWeeklyTodoRequest;
import com.newsainturtle.shadowmate.planner.dto.AddWeeklyTodoResponse;
import com.newsainturtle.shadowmate.planner.dto.UpdateWeeklyTodoContentRequest;
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

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeeklyPlannerServiceTest {

    @InjectMocks
    private WeeklyPlannerServiceImpl weeklyPlannerServiceImpl;

    @Mock
    private WeeklyRepository weeklyRepository;

    @Mock
    private WeeklyTodoRepository weeklyTodoRepository;

    private final String startDay = "2023-10-09";
    private final String endDay = "2023-10-15";
    private final String weeklyTodoContent = "자기소개서 제출하기";
    private final User user = User.builder()
            .id(1L)
            .email("test@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이")
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .withdrawal(false)
            .build();

    @Nested
    class 주차별할일등록 {

        @Test
        public void 실패_올바르지않은날짜_시작요일이월요일이아님() {
            //given
            final AddWeeklyTodoRequest request = AddWeeklyTodoRequest.builder()
                    .startDate("2023-10-10")
                    .endDate(endDay)
                    .weeklyTodoContent(weeklyTodoContent)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.addWeeklyTodo(user, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        public void 실패_올바르지않은날짜_일주일간격이아님() {
            //given
            final AddWeeklyTodoRequest request = AddWeeklyTodoRequest.builder()
                    .startDate(startDay)
                    .endDate("2023-10-16")
                    .weeklyTodoContent(weeklyTodoContent)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.addWeeklyTodo(user, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        public void 성공() {
            //given
            final AddWeeklyTodoRequest request = AddWeeklyTodoRequest.builder()
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
            final AddWeeklyTodoResponse addWeeklyTodoRequest = weeklyPlannerServiceImpl.addWeeklyTodo(user, request);

            //then
            assertThat(addWeeklyTodoRequest.getWeeklyTodoId()).isNotNull();

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

        @Test
        public void 실패_올바르지않은날짜_시작요일이월요일이아님() {
            //given
            final UpdateWeeklyTodoContentRequest request = UpdateWeeklyTodoContentRequest.builder()
                    .startDate("2023-10-10")
                    .endDate(endDay)
                    .weeklyTodoId(weeklyTodo.getId())
                    .weeklyTodoContent(changeWeeklyTodoContent)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        public void 실패_올바르지않은날짜_일주일간격이아님() {
            //given
            final UpdateWeeklyTodoContentRequest request = UpdateWeeklyTodoContentRequest.builder()
                    .startDate(startDay)
                    .endDate("2023-10-16")
                    .weeklyTodoId(weeklyTodo.getId())
                    .weeklyTodoContent(changeWeeklyTodoContent)
                    .build();

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_DATE);
        }

        @Test
        public void 실패_유효하지않은위클리할일() {
            //given
            final UpdateWeeklyTodoContentRequest request = UpdateWeeklyTodoContentRequest.builder()
                    .startDate(startDay)
                    .endDate(endDay)
                    .weeklyTodoId(weeklyTodo.getId())
                    .weeklyTodoContent(changeWeeklyTodoContent)
                    .build();
            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(null).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));

            //when
            final PlannerException result = assertThrows(PlannerException.class, () -> weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, request));

            //then
            assertThat(result.getErrorResult()).isEqualTo(PlannerErrorResult.INVALID_TODO);
        }

        @Test
        public void 성공() {
            //given
            final UpdateWeeklyTodoContentRequest request = UpdateWeeklyTodoContentRequest.builder()
                    .startDate(startDay)
                    .endDate(endDay)
                    .weeklyTodoId(weeklyTodo.getId())
                    .weeklyTodoContent(weeklyTodoContent)
                    .build();

            doReturn(weekly).when(weeklyRepository).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).findByIdAndWeekly(any(Long.class), any(Weekly.class));
            doReturn(weeklyTodo).when(weeklyTodoRepository).save(any(WeeklyTodo.class));

            //when
            weeklyPlannerServiceImpl.updateWeeklyTodoContent(user, request);

            //then

            //verify
            verify(weeklyRepository, times(1)).findByUserAndStartDayAndEndDay(any(), any(Date.class), any(Date.class));
            verify(weeklyTodoRepository, times(1)).findByIdAndWeekly(any(Long.class), any(Weekly.class));
            verify(weeklyTodoRepository, times(1)).save(any(WeeklyTodo.class));
        }

    }
}
