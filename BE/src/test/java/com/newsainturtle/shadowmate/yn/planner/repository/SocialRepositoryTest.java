package com.newsainturtle.shadowmate.yn.planner.repository;

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

import java.sql.Date;
import java.time.LocalDateTime;

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
                .dailyPlannerDay(Date.valueOf("2023-09-25"))
                .user(user)
                .build());
    }

    @Nested
    class 소셜공유 {
        final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";

        @Test
        void 소셜공유() {
            //given
            final Social social = Social.builder()
                    .dailyPlanner(dailyPlanner)
                    .socialImage(socialImage)
                    .build();
            //when
            final Social saveSocial = socialRepository.save(social);

            //then
            assertThat(saveSocial).isNotNull();
            assertThat(saveSocial.getSocialImage()).isNotNull().isEqualTo(socialImage);
            assertThat(saveSocial.getDailyPlanner()).isEqualTo(dailyPlanner);
        }

        @Test
        void 소셜재공유() {
            //given
            final String changeSocialImage = "https://d2u3dcdbebyaiu.cloudfront.net/uploads/atch_img/566/fcf7adfa96e5be280c293ed826ac88b5_res.jpeg";
            final Social saveSocial = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner)
                    .socialImage(socialImage)
                    .build());
            //when
            final Social social = Social.builder()
                    .id(saveSocial.getId())
                    .createTime(saveSocial.getCreateTime())
                    .dailyPlanner(saveSocial.getDailyPlanner())
                    .socialImage(changeSocialImage)
                    .build();
            final Social changeSocial = socialRepository.save(social);

            //then
            assertThat(changeSocial).isNotNull();
            assertThat(changeSocial.getCreateTime()).isNotEqualTo(changeSocial.getUpdateTime()).isEqualTo(saveSocial.getCreateTime());
            assertThat(changeSocial.getSocialImage()).isNotNull().isEqualTo(changeSocialImage);
            assertThat(changeSocial.getDailyPlanner()).isEqualTo(dailyPlanner);
        }
    }

    @Nested
    class 소셜공유확인 {
        final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";

        @Test
        void 소셜공유안함() {
            //given

            //when
            final Social findSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner);

            //then
            assertThat(findSocial).isNull();
        }

        @Test
        void 소셜공유함() {
            //given
            socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner)
                    .socialImage(socialImage)
                    .build());

            //when
            final Social findSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner);

            //then
            assertThat(findSocial).isNotNull();
            assertThat(findSocial.getSocialImage()).isNotNull().isEqualTo(socialImage);
            assertThat(findSocial.getDailyPlanner()).isEqualTo(dailyPlanner);
        }

        @Test
        void 소셜공유함_비공개상태() {
            //given
            socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner)
                    .socialImage(socialImage)
                    .deleteTime(LocalDateTime.now())
                    .build());

            //when
            final Social findSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner);

            //then
            assertThat(findSocial).isNull();
        }
    }
}
