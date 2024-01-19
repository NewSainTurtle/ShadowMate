package com.newsainturtle.shadowmate.planner_setting.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.user.entity.User;
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
@Table(name = "routine")
@AttributeOverride(name = "id", column = @Column(name = "routine_id"))
public class Routine extends CommonEntity {

    @Column(name = "routine_content", length = 50, nullable = false)
    private String routineContent;

    @Column(name = "start_day", length = 10, nullable = false)
    private String startDay;

    @Column(name = "end_day", length = 10, nullable = false)
    private String endDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "routine", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineTodo> routineTodos = new ArrayList<>();

    @OneToMany(mappedBy = "routine", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoutineDay> routineDays = new ArrayList<>();

    public void updateDayAndCategoryAndRoutineContent(final String startDay, final String endDay, final Category category, final String routineContent) {
        this.startDay = startDay;
        this.endDay = endDay;
        this.category = category;
        this.routineContent = routineContent;
    }
}
