package com.newsainturtle.shadowmate.planner_setting.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "dday")
@AttributeOverride(name = "id", column = @Column(name = "dday_id"))
public class Dday extends CommonEntity {

    @Column(name = "dday_date")
    private LocalDate ddayDate;

    @Column(name = "dday_title", length = 40, nullable = false)
    private String ddayTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateDdayDateAndTitle(final LocalDate ddayDate,
                                       final String ddayTitle) {
        this.ddayDate = ddayDate;
        this.ddayTitle = ddayTitle;
    }
}
