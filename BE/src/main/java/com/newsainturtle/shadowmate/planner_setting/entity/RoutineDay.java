package com.newsainturtle.shadowmate.planner_setting.entity;

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
@Table(name = "routine_day")
@AttributeOverride(name = "id", column = @Column(name = "routine_day_id"))
public class RoutineDay extends CommonEntity {

    @Column(name = "day", length = 1, nullable = false)
    private String day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;
}
