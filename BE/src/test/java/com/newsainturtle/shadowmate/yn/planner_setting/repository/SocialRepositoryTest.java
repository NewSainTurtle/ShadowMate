package com.newsainturtle.shadowmate.yn.planner_setting.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.social.entity.Social;
import com.newsainturtle.shadowmate.social.repository.SocialRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SocialRepositoryTest {

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    private User user;
    private DailyPlanner dailyPlanner;
    private DailyPlanner dailyPlanner2;
    private DailyPlanner dailyPlanner3;

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        dailyPlanner = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay("2023-09-25")
                .user(user)
                .build());
        dailyPlanner2 = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay("2023-09-26")
                .user(user)
                .build());
        dailyPlanner3 = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay("2023-09-27")
                .user(user)
                .build());
    }

    @Nested
    class 플래너공개여부설정에따른_소셜 {
        final String socialImage = "https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/566/fcf7adfa96e5be280c293ed826ac88b5_res.jpeg";

        @Test
        void 소셜숨기기() {
            //given
            final Social social = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .build());
            final Social social2 = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner2)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .build());
            final Social social3 = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner3)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .build());

            //when
            final List<DailyPlanner> dailyPlanners = dailyPlannerRepository.findAllByUser(user);
            final LocalDateTime localDateTime = LocalDateTime.now();
            socialRepository.updateDeleteTimeAll(localDateTime, dailyPlanners);
            final Social checkSocial = socialRepository.findById(social.getId()).orElse(null);
            final Social checkSocial2 = socialRepository.findById(social2.getId()).orElse(null);
            final Social checkSocial3 = socialRepository.findById(social3.getId()).orElse(null);

            //then
            assertThat(checkSocial).isNotNull();
            assertThat(checkSocial2).isNotNull();
            assertThat(checkSocial3).isNotNull();
            assertThat(checkSocial.getDeleteTime()).isNotNull().isEqualTo(checkSocial2.getDeleteTime()).isEqualTo(checkSocial3.getDeleteTime());
        }

        @Test
        void 소셜보이기() {
            //given
            final LocalDateTime localDateTime = LocalDateTime.now();

            final Social social = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .deleteTime(localDateTime)
                    .build());
            final Social social2 = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner2)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner2.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .deleteTime(localDateTime)
                    .build());
            final Social social3 = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner3)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner3.getDailyPlannerDay())
                    .ownerId(user.getId())
                    .deleteTime(localDateTime)
                    .build());

            //when
            final List<DailyPlanner> dailyPlanners = dailyPlannerRepository.findAllByUser(user);
            socialRepository.updateDeleteTimeAll(null, dailyPlanners);
            final Social checkSocial = socialRepository.findById(social.getId()).orElse(null);
            final Social checkSocial2 = socialRepository.findById(social2.getId()).orElse(null);
            final Social checkSocial3 = socialRepository.findById(social3.getId()).orElse(null);

            //then
            assertThat(checkSocial).isNotNull();
            assertThat(checkSocial2).isNotNull();
            assertThat(checkSocial3).isNotNull();
            assertThat(checkSocial.getDeleteTime()).isNull();
            assertThat(checkSocial2.getDeleteTime()).isNull();
            assertThat(checkSocial3.getDeleteTime()).isNull();
        }
    }
}
