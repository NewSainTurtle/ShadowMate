package com.newsainturtle.shadowmate.planner_setting.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.planner.entity.Todo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "routine_todo",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"routine_id", "daily_planner_day"}
                )
        })
@AttributeOverride(name = "id", column = @Column(name = "routine_todo_id"))
public class RoutineTodo extends CommonEntity {

    @Column(name = "day", length = 1, nullable = false)
    private String day;

    @Column(name = "daily_planner_day", length = 10, nullable = false)
    private String dailyPlannerDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;

    public void updateTodo(final Todo todo) {
        this.todo = todo;
    }

    public void setRoutine(Routine routine) {
        if (this.routine != null) {
            this.routine.getRoutineTodos().remove(this);
        }

        this.routine = routine;
        if (routine != null) {
            routine.getRoutineTodos().add(this);
        }
    }
}