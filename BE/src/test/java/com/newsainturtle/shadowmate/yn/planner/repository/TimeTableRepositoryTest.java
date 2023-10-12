package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.TimeTable;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.planner.repository.TimeTableRepository;
import com.newsainturtle.shadowmate.planner.repository.TodoRepository;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TimeTableRepositoryTest {

    @Autowired
    private TimeTableRepository timeTableRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    @Autowired
    private TodoRepository todoRepository;

    private User user;
    private DailyPlanner dailyPlanner;
    private Todo todo;
    final private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    public void init() {
        user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user)
                .build());
        todo = todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("수능완성 수학 과목별 10문제")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .build());
    }

    @Test
    public void 타임테이블등록() {
        //given
        final LocalDateTime startTime = LocalDateTime.parse("2023-10-06 16:10", formatter);
        final LocalDateTime endTime = LocalDateTime.parse("2023-10-06 18:30", formatter);
        final TimeTable timeTable = TimeTable.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
        //when
        todo.setTimeTable(timeTable);
        final Todo findTodo = todoRepository.findById(todo.getId()).orElse(null);

        //then
        assertThat(findTodo).isNotNull();
        assertThat(findTodo.getTimeTable()).isNotNull();
        assertThat(findTodo.getTimeTable().getStartTime()).isEqualTo(startTime);
        assertThat(findTodo.getTimeTable().getEndTime()).isEqualTo(endTime);
        assertThat(findTodo.getTimeTable().getTodo()).isEqualTo(todo);
    }

    @Test
    public void 타임테이블삭제() {
        //given
        final LocalDateTime startTime = LocalDateTime.parse("2023-10-06 16:10", formatter);
        final LocalDateTime endTime = LocalDateTime.parse("2023-10-06 18:30", formatter);
        final TimeTable timeTable = TimeTable.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();
        todo.setTimeTable(timeTable);

        //when
        final Todo findTodo = todoRepository.findByIdAndDailyPlanner(todo.getId(), dailyPlanner);
        final long timeTableId = findTodo.getTimeTable().getId();
        findTodo.setTimeTable(null);
        timeTableRepository.deleteById(timeTableId);

        //then
        assertThat(findTodo.getTimeTable()).isNull();
    }

}
