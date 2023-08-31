package com.newsainturtle.shadowmate.user.entity;

import com.newsainturtle.shadowmate.common.entity.CommonEntity;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@SuperBuilder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user")
@AttributeOverride(name = "id", column = @Column(name= "user_id"))
public class User extends CommonEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 60, nullable = false)
    private String password;

    @Column(name = "social_login", nullable = false)
    private Boolean socialLogin;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(name = "status_message", length = 40)
    private String statusMessage;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean withdrawal;

    @Column(name = "planner_access_scope")
    @Enumerated(EnumType.STRING)
    private PlannerAccessScope plannerAccessScope;
}
