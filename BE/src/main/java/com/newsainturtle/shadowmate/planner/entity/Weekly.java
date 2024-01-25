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
@Table(name = "weekly",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"start_day", "end_day", "user_id"}
                )
        })
@AttributeOverride(name = "id", column = @Column(name = "weekly_id"))
public class Weekly extends CommonEntity {

    @Column(name = "start_day", length = 10, nullable = false)
    private String startDay;

    @Column(name = "end_day", length = 10, nullable = false)
    private String endDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
