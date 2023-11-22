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
@Table(name = "social")
@AttributeOverride(name = "id", column = @Column(name = "social_id"))
public class Social extends CommonEntity {

    @OneToOne
    @JoinColumn(name = "daily_planner_id")
    private DailyPlanner dailyPlanner;

    @Column(name = "social_image")
    private String socialImage;

    public void updateSocial(final String socialImage) {
        this.socialImage = socialImage;
    }
}
