package com.newsainturtle.shadowmate.planner.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.planner.enums.TodoStatus;
import com.newsainturtle.shadowmate.planner_setting.entity.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "todo_index", nullable = false)
    private Double todoIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_planner_id")
    private DailyPlanner dailyPlanner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "todo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeTable> timeTables = new ArrayList<>();

    public void updateTodoIndex(final Double todoIndex) {
        this.todoIndex = todoIndex;
    }

    public void clearTimeTables() {
        this.timeTables = new ArrayList<>();
    }
}
