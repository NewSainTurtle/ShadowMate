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
@Table(name = "daily_planner")
@AttributeOverride(name = "id", column = @Column(name = "daily_planner_id"))
public class DailyPlanner extends CommonEntity {

    @Column(name = "daily_planner_day", length = 10, nullable = false)
    private String dailyPlannerDay;

    @Column(name = "today_goal", length = 100)
    private String todayGoal;

    @Column(length = 200)
    private String retrospection;

    @Column(name = "retrospection_image")
    private String retrospectionImage;

    @Column(name = "tomorrow_goal", length = 100)
    private String tomorrowGoal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateTodayGoal(final String todayGoal) {
        this.todayGoal = todayGoal;
    }

    public void updateTomorrowGoal(final String tomorrowGoal) {
        this.tomorrowGoal = tomorrowGoal;
    }

    public void updateRetrospection(final String retrospection) {
        this.retrospection = retrospection;
    }

    public void updateRetrospectionImage(final String retrospectionImage) {
        this.retrospectionImage = retrospectionImage;
    }
}