package com.newsainturtle.shadowmate.social.repository;

import com.newsainturtle.shadowmate.planner.entity.DailyPlanner;
import com.newsainturtle.shadowmate.planner.entity.DailyPlannerLike;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerLikeRepository;
import com.newsainturtle.shadowmate.planner.repository.DailyPlannerRepository;
import com.newsainturtle.shadowmate.social.entity.Social;
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
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SocialRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private DailyPlannerRepository dailyPlannerRepository;

    @Autowired
    private DailyPlannerLikeRepository dailyPlannerLikeRepository;

    private User user1;
    private User user2;
    private DailyPlanner dailyPlanner1;
    private DailyPlanner dailyPlanner2;
    private DailyPlanner dailyPlanner3;
    private Social social1;
    private Social social2;
    private Social social3;
    private final String socialImage = "https://i.pinimg.com/564x/62/00/71/620071d0751e8cd562580a83ec834f7e.jpg";

    @BeforeEach
    public void init() {
        user1 = userRepository.save(User.builder()
                .email("yntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        user2 = userRepository.save(User.builder()
                .email("jntest@shadowmate.com")
                .password("yntest1234")
                .socialLogin(SocialType.BASIC)
                .nickname("토끼")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
        dailyPlanner1 = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay("2000-09-25")
                .user(user1)
                .build());
        dailyPlanner2 = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay("2000-09-25")
                .user(user2)
                .build());
        dailyPlanner3 = dailyPlannerRepository.save(DailyPlanner.builder()
                .dailyPlannerDay("2000-09-27")
                .user(user2)
                .build());
        social1 = Social.builder()
                .dailyPlanner(dailyPlanner1)
                .socialImage(socialImage)
                .dailyPlannerDay(dailyPlanner1.getDailyPlannerDay())
                .ownerId(user1.getId())
                .build();
        social2 = Social.builder()
                .dailyPlanner(dailyPlanner2)
                .socialImage(socialImage)
                .dailyPlannerDay(dailyPlanner2.getDailyPlannerDay())
                .ownerId(user2.getId())
                .build();
        social3 = Social.builder()
                .dailyPlanner(dailyPlanner3)
                .socialImage(socialImage)
                .dailyPlannerDay(dailyPlanner3.getDailyPlannerDay())
                .ownerId(user2.getId())
                .build();
    }

    @Nested
    class 일반_소셜조회 {
        @Test
        void 성공_없음() {
            // given

            // when
            final List<Social> result = socialRepository.findAllByDeleteTimeIsNullSortLatest(PageRequest.of(0, 6));

            // then
            assertThat(result).isNotNull();
        }

        @Test
        void 성공_최신순() {
            // given
            socialRepository.save(social1);
            socialRepository.save(social2);
            socialRepository.save(social3);

            // when
            final List<Social> result = socialRepository.findAllByDeleteTimeIsNullSortLatest(PageRequest.of(0, 6));

            // then
            assertThat(result).isNotEmpty();
        }

        @Test
        void 성공_인기순() {
            // given
            dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                    .dailyPlanner(dailyPlanner2)
                    .user(user1)
                    .build());
            socialRepository.save(social1);
            socialRepository.save(social2);
            socialRepository.save(social3);

            // when
            final List<Social> result = socialRepository.findAllByDeleteTimeIsNullSortLatest(PageRequest.of(0, 6));

            // then
            assertThat(result).isNotEmpty();
        }

        @Test
        void 성공_기간_없음() {
            // given
            socialRepository.save(social1);
            socialRepository.save(social2);
            socialRepository.save(social3);

            // when
            final List<Social> result = socialRepository.findAllByDeleteTimeIsNullAndPeriodSortLatest("2000-09-23", "2000-09-24", PageRequest.of(0, 6));

            // then
            assertThat(result).isNotNull().isEmpty();
            ;
        }

        @Test
        void 성공_기간_있음() {
            // given
            socialRepository.save(social1);
            socialRepository.save(social2);
            socialRepository.save(social3);

            // when
            final List<Social> result = socialRepository.findAllByDeleteTimeIsNullAndPeriodSortLatest("2000-09-23", "2000-09-25", PageRequest.of(0, 6));

            // then
            assertThat(result).isNotNull().hasSize(2);
        }

    }

    @Nested
    class ID검색_소셜조회 {
        @Test
        void 성공_없음() {
            // given
            socialRepository.save(social1);

            // when
            final List<Social> result = socialRepository.findAllByOwnerIdAndDeleteTimeIsNullSortLatest(user2.getId(), PageRequest.of(0, 6));

            // then
            assertThat(result).isNotNull().isEmpty();
        }

        @Test
        void 성공_최신순() {
            // given
            socialRepository.save(social1);
            socialRepository.save(social2);
            socialRepository.save(social3);

            // when
            final List<Social> result = socialRepository.findAllByOwnerIdAndDeleteTimeIsNullSortLatest(user2.getId(), PageRequest.of(0, 6));

            // then
            assertThat(result).isNotEmpty().hasSize(2);
            assertThat(result.get(0).getDailyPlannerDay()).isEqualTo("2000-09-27");
        }

        @Test
        void 성공_인기순() {
            // given
            socialRepository.save(social1);
            socialRepository.save(social2);
            socialRepository.save(social3);
            dailyPlannerLikeRepository.save(DailyPlannerLike.builder()
                    .dailyPlanner(dailyPlanner2)
                    .user(user1)
                    .build());

            // when
            final List<Social> result = socialRepository.findAllByOwnerIdAndDeleteTimeIsNullSortPopularity(user2.getId(), PageRequest.of(0, 6));

            // then
            assertThat(result).isNotEmpty().hasSize(2);
            assertThat(result.get(0).getDailyPlannerDay()).isEqualTo("2000-09-25");
        }

        @Test
        void 성공_기간_없음() {
            // given
            socialRepository.save(social1);
            socialRepository.save(social2);
            socialRepository.save(social3);

            // when
            final List<Social> result = socialRepository.findAllByOwnerIdAndDeleteTimeIsNullAndPeriodSortLatest(user1.getId(), "2000-09-23", "2000-09-24", PageRequest.of(0, 6));

            // then
            assertThat(result).isNotNull().isEmpty();
            ;
        }

        @Test
        void 성공_기간_있음() {
            // given
            socialRepository.save(social1);
            socialRepository.save(social2);
            socialRepository.save(social3);

            // when
            final List<Social> result = socialRepository.findAllByOwnerIdAndDeleteTimeIsNullAndPeriodSortLatest(user1.getId(), "2000-09-23", "2000-09-25", PageRequest.of(0, 6));

            // then
            assertThat(result).isNotNull().hasSize(1);
        }
    }

    @Nested
    class 소셜삭제 {
        @Test
        void 성공() {
            // given
            final Long socialId = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner1)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner1.getDailyPlannerDay())
                    .ownerId(user1.getId())
                    .build()).getId();

            // when
            socialRepository.deleteByIdAndOwnerId(socialId, user1.getId());
            final Optional<Social> findSocial = socialRepository.findById(socialId);

            // then
            assertThat(socialId).isNotNull();
            assertThat(findSocial).isNotPresent();
        }
    }

    @Test
    void 소셜공유() {
        //given

        //when
        final Social saveSocial = socialRepository.save(social1);

        //then
        assertThat(saveSocial).isNotNull();
        assertThat(saveSocial.getSocialImage()).isNotNull().isEqualTo(socialImage);
        assertThat(saveSocial.getDailyPlanner()).isEqualTo(dailyPlanner1);
        assertThat(saveSocial.getDailyPlannerDay()).isEqualTo(dailyPlanner1.getDailyPlannerDay());
        assertThat(saveSocial.getOwnerId()).isEqualTo(dailyPlanner1.getUser().getId());
    }

    @Nested
    class 소셜공유확인 {

        @Test
        void 소셜공유안함() {
            //given

            //when
            final Social findSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner1);

            //then
            assertThat(findSocial).isNull();
        }

        @Test
        void 소셜공유함() {
            //given
            socialRepository.save(social1);

            //when
            final Social findSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner1);

            //then
            assertThat(findSocial).isNotNull();
            assertThat(findSocial.getSocialImage()).isNotNull().isEqualTo(socialImage);
            assertThat(findSocial.getDailyPlanner()).isEqualTo(dailyPlanner1);
            assertThat(findSocial.getDailyPlannerDay()).isEqualTo(dailyPlanner1.getDailyPlannerDay());
            assertThat(findSocial.getOwnerId()).isEqualTo(dailyPlanner1.getUser().getId());
        }

        @Test
        void 소셜공유함_비공개상태() {
            //given
            socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner1)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner1.getDailyPlannerDay())
                    .ownerId(user1.getId())
                    .deleteTime(LocalDateTime.now())
                    .build());

            //when
            final Social findSocial = socialRepository.findByDailyPlannerAndDeleteTimeIsNull(dailyPlanner1);

            //then
            assertThat(findSocial).isNull();
        }
    }

    @Nested
    class 플래너공개여부설정에따른_소셜 {

        @Test
        void 소셜숨기기() {
            //given
            final Social social = socialRepository.save(social1);

            //when
            final List<DailyPlanner> dailyPlanners = dailyPlannerRepository.findAllByUser(user1);
            final LocalDateTime localDateTime = LocalDateTime.now();
            socialRepository.updateDeleteTimeAll(localDateTime, dailyPlanners);
            final Social checkSocial = socialRepository.findById(social.getId()).orElse(null);

            //then
            assertThat(checkSocial).isNotNull();
            assertThat(checkSocial.getDailyPlanner()).isNotNull();
        }

        @Test
        void 소셜보이기() {
            //given
            final LocalDateTime localDateTime = LocalDateTime.now();
            final Social social2 = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner2)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner2.getDailyPlannerDay())
                    .ownerId(user2.getId())
                    .deleteTime(localDateTime)
                    .build());
            final Social social3 = socialRepository.save(Social.builder()
                    .dailyPlanner(dailyPlanner3)
                    .socialImage(socialImage)
                    .dailyPlannerDay(dailyPlanner3.getDailyPlannerDay())
                    .ownerId(user2.getId())
                    .deleteTime(localDateTime)
                    .build());

            //when
            final List<DailyPlanner> dailyPlanners = dailyPlannerRepository.findAllByUser(user2);
            socialRepository.updateDeleteTimeAll(null, dailyPlanners);
            final Social checkSocial2 = socialRepository.findById(social2.getId()).orElse(null);
            final Social checkSocial3 = socialRepository.findById(social3.getId()).orElse(null);

            //then
            assertThat(checkSocial2).isNotNull();
            assertThat(checkSocial3).isNotNull();
            assertThat(checkSocial2.getDeleteTime()).isNull();
            assertThat(checkSocial3.getDeleteTime()).isNull();
        }
    }
}
