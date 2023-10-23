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

import java.sql.Date;
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

    private final Date startDay = Date.valueOf("2023-10-09");
    private final Date endDay = Date.valueOf("2023-10-15");
    private Weekly weekly;

    @BeforeEach
    void init() {
        User user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
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
                .weeklyTodoContent("자기소개서 제출하기")
                .weeklyTodoStatus(false)
                .build());


        //then
        assertThat(weeklyTodo).isNotNull();
        assertThat(weeklyTodo.getWeekly()).isEqualTo(weekly);
        assertThat(weeklyTodo.getWeeklyTodoContent()).isEqualTo("자기소개서 제출하기");
        assertThat(weeklyTodo.getWeeklyTodoStatus()).isFalse();
    }

    @Test
    void 주차별할일내용수정() {
        //given
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.save(WeeklyTodo.builder()
                .weekly(weekly)
                .weeklyTodoContent("자기소개서 제출하기")
                .weeklyTodoStatus(false)
                .build());
        //when
        final WeeklyTodo changeWeeklyTodo = weeklyTodoRepository.save(
                WeeklyTodo.builder()
                        .id(weeklyTodo.getId())
                        .createTime(weeklyTodo.getCreateTime())
                        .weekly(weeklyTodo.getWeekly())
                        .weeklyTodoContent("자기소개서 첨삭하기")
                        .weeklyTodoStatus(weeklyTodo.getWeeklyTodoStatus())
                        .build()
        );


        //then
        assertThat(changeWeeklyTodo).isNotNull();
        assertThat(changeWeeklyTodo.getWeekly()).isEqualTo(weeklyTodo.getWeekly());
        assertThat(changeWeeklyTodo.getWeeklyTodoStatus()).isFalse();
        assertThat(changeWeeklyTodo.getWeeklyTodoContent()).isEqualTo("자기소개서 첨삭하기");
        assertThat(changeWeeklyTodo.getCreateTime()).isNotEqualTo(changeWeeklyTodo.getUpdateTime());
    }

    @Test
    void 주차별할일상태수정() {
        //given
        final WeeklyTodo weeklyTodo = weeklyTodoRepository.save(WeeklyTodo.builder()
                .weekly(weekly)
                .weeklyTodoContent("자기소개서 제출하기")
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
                .weeklyTodoContent("자기소개서 제출하기")
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
            assertThat(weeklyTodoList).isNotNull();
            assertThat(weeklyTodoList).isEmpty();
        }

        @Test
        void 주차별할일데이터있음() {
            //given
            weeklyTodoRepository.save(WeeklyTodo.builder()
                    .weekly(weekly)
                    .weeklyTodoContent("자기소개서 제출하기")
                    .weeklyTodoStatus(false)
                    .build());
            weeklyTodoRepository.save(WeeklyTodo.builder()
                    .weekly(weekly)
                    .weeklyTodoContent("마트에서 장보기")
                    .weeklyTodoStatus(true)
                    .build());
            //when
            final List<WeeklyTodo> weeklyTodoList = weeklyTodoRepository.findAllByWeekly(weekly);

            //then
            assertThat(weeklyTodoList).isNotNull();
            assertThat(weeklyTodoList).hasSize(2);
        }
    }

}
