package com.newsainturtle.shadowmate.planner_setting.entity;

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
    @JoinColumn(name = "user_id")
    private User user;
}
