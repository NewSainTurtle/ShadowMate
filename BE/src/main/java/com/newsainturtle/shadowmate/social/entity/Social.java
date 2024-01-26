package com.newsainturtle.shadowmate.social.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "social",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"owner_id", "daily_planner_day"}
                )
        })
@AttributeOverride(name = "id", column = @Column(name = "social_id"))
public class Social extends CommonEntity {

    @OneToOne
    @JoinColumn(name = "daily_planner_id", nullable = false)
    private DailyPlanner dailyPlanner;

    @Column(name = "social_image", nullable = false)
    private String socialImage;

    @Column(name = "daily_planner_day", length = 10, nullable = false)
    private String dailyPlannerDay;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

}
