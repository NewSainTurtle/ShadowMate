package com.newsainturtle.shadowmate.planner.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Date;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "weekly")
@AttributeOverride(name = "id", column = @Column(name = "weekly_id"))
public class Weekly extends CommonEntity {

    @Column(name = "start_day", nullable = false)
    private Date startDay;

    @Column(name = "end_day", nullable = false)
    private Date endDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
