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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TimeTableRepositoryTest {

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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay("2023-09-25")
                .user(user)
                .build());
        todo = todoRepository.save(Todo.builder()
                .category(null)
                .todoContent("수능완성 수학 과목별 10문제")
                .todoStatus(TodoStatus.EMPTY)
                .dailyPlanner(dailyPlanner)
                .todoIndex(100000D)
                .timeTables(new ArrayList<>())
                .build());
    }

    @Test
    void 타임테이블등록() {
        //given
        final LocalDateTime startTime = LocalDateTime.parse("2023-09-25 16:10", formatter);
        final LocalDateTime endTime = LocalDateTime.parse("2023-09-25 18:30", formatter);
        final TimeTable timeTable = timeTableRepository.save(TimeTable.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build());
        timeTable.setTodo(todo);

        //when
        final Optional<TimeTable> findTimeTable = timeTableRepository.findById(timeTable.getId());

        //then
        assertThat(findTimeTable).isPresent();
        assertThat(findTimeTable.get()).isNotNull();
        assertThat(findTimeTable.get().getStartTime()).isEqualTo(startTime);
        assertThat(findTimeTable.get().getEndTime()).isEqualTo(endTime);
        assertThat(findTimeTable.get().getTodo()).isEqualTo(todo);
    }

    @Test
    void 타임테이블조회() {
        //given
        final LocalDateTime startTime = LocalDateTime.parse("2023-09-25 16:10", formatter);
        final LocalDateTime endTime = LocalDateTime.parse("2023-09-25 18:30", formatter);
        final TimeTable timeTable = timeTableRepository.save(TimeTable.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build());
        timeTable.setTodo(todo);

        //when
        final TimeTable findTimeTable = timeTableRepository.findByIdAndTodoId(timeTable.getId(), todo.getId());

        //then
        assertThat(findTimeTable).isNotNull();
        assertThat(findTimeTable.getStartTime()).isEqualTo(startTime);
        assertThat(findTimeTable.getEndTime()).isEqualTo(endTime);
        assertThat(findTimeTable.getTodo()).isEqualTo(todo);
    }

    @Test
    void 타임테이블삭제() {
        //given
        final LocalDateTime startTime = LocalDateTime.parse("2023-09-25 16:10", formatter);
        final LocalDateTime endTime = LocalDateTime.parse("2023-09-25 18:30", formatter);
        final TimeTable timeTable = timeTableRepository.save(TimeTable.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build());

        //when
        timeTable.setTodo(null);
        timeTableRepository.deleteById(timeTable.getId());
        final Optional<TimeTable> findTimeTable = timeTableRepository.findById(timeTable.getId());

        //then
        assertThat(findTimeTable).isNotPresent();
    }

    @Test
    void 타임테이블삭제_할일삭제시한번에삭제() {
        //given
        final TimeTable timeTable = timeTableRepository.save(TimeTable.builder()
                .startTime(LocalDateTime.parse("2023-09-25 16:10", formatter))
                .endTime(LocalDateTime.parse("2023-09-25 18:30", formatter))
                .todo(todo)
                .build());
        timeTableRepository.save(TimeTable.builder()
                .startTime(LocalDateTime.parse("2023-09-25 20:10", formatter))
                .endTime(LocalDateTime.parse("2023-09-25 21:30", formatter))
                .todo(todo)
                .build());

        //when
        timeTableRepository.deleteAllByTodoId(todo.getId());
        todo.clearTimeTables();

        final Optional<TimeTable> findTimeTable = timeTableRepository.findById(timeTable.getId());

        //then
        assertThat(findTimeTable).isNotPresent();
    }

}
