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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class WeeklyTodoRepositoryTest {

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
    public void init() {
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
    public void 주차별할일등록() {
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
}
