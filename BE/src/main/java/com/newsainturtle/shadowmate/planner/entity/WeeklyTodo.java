package com.newsainturtle.shadowmate.planner.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "weekly_todo")
@AttributeOverride(name = "id", column = @Column(name = "weekly_todo_id"))
public class WeeklyTodo extends CommonEntity {

    @Column(name = "weekly_todo_content", nullable = false, length = 50)
    private String weeklyTodoContent;

    @Column(name = "weekly_todo_status", nullable = false)
    private Boolean weeklyTodoStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_id")
    private Weekly weekly;

    public void updateWeeklyTodoContent(final String weeklyTodoContent) {
        this.weeklyTodoContent = weeklyTodoContent;
    }

    public void updateWeeklyTodoStatus(final boolean weeklyTodoStatus) {
        this.weeklyTodoStatus = weeklyTodoStatus;
    }
}
