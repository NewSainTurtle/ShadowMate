package com.newsainturtle.shadowmate.user.entity;

import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(length = 30, unique = true, nullable = false)
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

    @Builder
    public User(Long userId, String email, String password, Boolean socialLogin, String profileImage, String nickname, String statusMessage, Boolean withdrawal, PlannerAccessScope plannerAccessScope) {
        this.userId = userId;
        this.email = email;
        this.password = new BCryptPasswordEncoder().encode(password);
        this.socialLogin = socialLogin;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.statusMessage = statusMessage;
        this.withdrawal = withdrawal;
        this.plannerAccessScope = plannerAccessScope;
    }

    public void setPassword(String password){
        this.password = new BCryptPasswordEncoder().encode(password);
    }
  
}
