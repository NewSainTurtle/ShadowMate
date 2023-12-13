package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.Weekly;
import com.newsainturtle.shadowmate.planner.entity.WeeklyTodo;
import com.newsainturtle.shadowmate.planner.repository.WeeklyRepository;
import com.newsainturtle.shadowmate.planner.repository.WeeklyTodoRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WeeklyTodoRepositoryTest {

    @Autowired
    private WeeklyTodoRepository weeklyTodoRepository;

    @Autowired
    private WeeklyRepository weeklyRepository;

    @Autowired
    private UserRepository userRepository;

    private final String startDay = "2023-10-09";
    private final String endDay = "2023-10-15";
    private Weekly weekly;
    private final String weeklyTodoContent = "지원서 작성하기";

    @BeforeEach
    void init() {
        User user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        weekly = weeklyRepository.save(Weekly.builder()
                .startDay(startDay)
                .endDay(endDay)
                .user(user)
                .build());
    }

    @Test
    void 주차별할일등록() {
        //given

        //when
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.save(WeeklyTodo.builder()
                .weekly(weekly)
                .weeklyTodoContent(weeklyTodoContent)
                .weeklyTodoStatus(false)
                .build());


        //then
        assertThat(weeklyTodo).isNotNull();
        assertThat(weeklyTodo.getWeekly()).isEqualTo(weekly);
        assertThat(weeklyTodo.getWeeklyTodoContent()).isEqualTo(weeklyTodoContent);
        assertThat(weeklyTodo.getWeeklyTodoStatus()).isFalse();
    }

    @Test
    void 주차별할일내용수정() {
        //given
        final String changeWeeklyTodoContent = "자기소개서 첨삭하기";
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.save(WeeklyTodo.builder()
                .weekly(weekly)
                .weeklyTodoContent(weeklyTodoContent)
                .weeklyTodoStatus(false)
                .build());
        //when
        final WeeklyTodo changeWeeklyTodo = weeklyTodoRepository.save(
                WeeklyTodo.builder()
                        .id(weeklyTodo.getId())
                        .createTime(weeklyTodo.getCreateTime())
                        .weekly(weeklyTodo.getWeekly())
                        .weeklyTodoContent(changeWeeklyTodoContent)
                        .weeklyTodoStatus(weeklyTodo.getWeeklyTodoStatus())
                        .build()
        );


        //then
        assertThat(changeWeeklyTodo).isNotNull();
        assertThat(changeWeeklyTodo.getWeekly()).isEqualTo(weeklyTodo.getWeekly());
        assertThat(changeWeeklyTodo.getWeeklyTodoStatus()).isFalse();
        assertThat(changeWeeklyTodo.getWeeklyTodoContent()).isEqualTo(changeWeeklyTodoContent);
        assertThat(changeWeeklyTodo.getCreateTime()).isNotEqualTo(changeWeeklyTodo.getUpdateTime());
    }

    @Test
    void 주차별할일상태수정() {
        //given
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.save(WeeklyTodo.builder()
                .weekly(weekly)
                .weeklyTodoContent(weeklyTodoContent)
                .weeklyTodoStatus(false)
                .build());
        //when
        final WeeklyTodo changeWeeklyTodo = weeklyTodoRepository.save(
                WeeklyTodo.builder()
                        .id(weeklyTodo.getId())
                        .createTime(weeklyTodo.getCreateTime())
                        .weekly(weeklyTodo.getWeekly())
                        .weeklyTodoContent(weeklyTodo.getWeeklyTodoContent())
                        .weeklyTodoStatus(true)
                        .build()
        );


        //then
        assertThat(changeWeeklyTodo).isNotNull();
        assertThat(changeWeeklyTodo.getWeekly()).isEqualTo(weeklyTodo.getWeekly());
        assertThat(changeWeeklyTodo.getWeeklyTodoContent()).isEqualTo(weeklyTodo.getWeeklyTodoContent());
        assertThat(changeWeeklyTodo.getWeeklyTodoStatus()).isTrue();
        assertThat(changeWeeklyTodo.getCreateTime()).isNotEqualTo(changeWeeklyTodo.getUpdateTime());
    }

    @Test
    void 주차별할일삭제() {
        //given
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.save(WeeklyTodo.builder()
                .weekly(weekly)
                .weeklyTodoContent(weeklyTodoContent)
                .weeklyTodoStatus(false)
                .build());
        //when
        weeklyTodoRepository.deleteByIdAndWeekly(weeklyTodo.getId(), weekly);
        final WeeklyTodo findWeeklyTodo = weeklyTodoRepository.findByIdAndWeekly(weeklyTodo.getId(), weekly);

        //then
        assertThat(findWeeklyTodo).isNull();
    }

    @Nested
    class 주차별할일목록조회 {
        @Test
        void 주차별할일데이터없음() {
            //given

            //when
            final List<WeeklyTodo> weeklyTodoList = weeklyTodoRepository.findAllByWeekly(weekly);

            //then
            assertThat(weeklyTodoList).isNotNull().isEmpty();
        }

        @Test
        void 주차별할일데이터있음() {
            //given
            weeklyTodoRepository.save(WeeklyTodo.builder()
                    .weekly(weekly)
                    .weeklyTodoContent(weeklyTodoContent)
                    .weeklyTodoStatus(false)
                    .build());
            weeklyTodoRepository.save(WeeklyTodo.builder()
                    .weekly(weekly)
                    .weeklyTodoContent(weeklyTodoContent)
                    .weeklyTodoStatus(true)
                    .build());
            //when
            final List<WeeklyTodo> weeklyTodoList = weeklyTodoRepository.findAllByWeekly(weekly);

            //then
            assertThat(weeklyTodoList).isNotNull().hasSize(2);
        }
    }

}
