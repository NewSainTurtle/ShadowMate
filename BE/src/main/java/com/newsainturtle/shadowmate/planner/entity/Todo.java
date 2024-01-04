package com.newsainturtle.shadowmate.planner.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "todo")
@AttributeOverride(name = "id", column = @Column(name = "todo_id"))
public class Todo extends CommonEntity {

    @Column(name = "todo_content", length = 50, nullable = false)
    private String todoContent;

    @Column(name = "todo_status")
    @Enumerated(EnumType.STRING)
    private TodoStatus todoStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_planner_id")
    private DailyPlanner dailyPlanner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "timeTable_id")
    private TimeTable timeTable;

    public void setTimeTable(TimeTable timeTable) {
        this.timeTable = timeTable;
        if (timeTable != null) {
            timeTable.setTodo(this);
        }
    }

    public void updateTodoContentAndCategoryAndStatus(final String todoContent,
                                                      final Category category,
                                                      final TodoStatus todoStatus) {
        this.todoContent = todoContent;
        this.category = category;
        this.todoStatus = todoStatus;

    }
}
