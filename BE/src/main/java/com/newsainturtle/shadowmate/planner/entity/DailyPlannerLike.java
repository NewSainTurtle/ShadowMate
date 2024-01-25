package com.newsainturtle.shadowmate.planner.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "daily_planner_like",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id", "daily_planner_id"}
                )
        })
@AttributeOverride(name = "id", column = @Column(name = "daily_planner_like_id"))
public class DailyPlannerLike extends CommonEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_planner_id", nullable = false)
    private DailyPlanner dailyPlanner;

}
