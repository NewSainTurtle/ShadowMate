package com.newsainturtle.shadowmate.yn.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
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
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DdayRepositoryTest {

    @Autowired
    private DdayRepository ddayRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void init() {
        user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());
    }

    @Test
    void 디데이등록() {
        //given
        final Dday dday = Dday.builder()
                .ddayTitle("생일")
                .ddayDate(Date.valueOf("2023-02-09"))
                .user(user)
                .build();

        //when
        final Dday saveDday = ddayRepository.save(dday);

        //then
        assertThat(saveDday).isNotNull();
        assertThat(saveDday.getDdayDate()).isEqualTo("2023-02-09");
        assertThat(saveDday.getDdayTitle()).isEqualTo("생일");
        assertThat(saveDday.getUser()).isEqualTo(user);
    }

    @Test
    void 디데이목록조회_미래순() {
        //given
        ddayRepository.save(Dday.builder()
                .ddayTitle("생일")
                .ddayDate(Date.valueOf("2023-02-09"))
                .user(user)
                .build());
        ddayRepository.save(Dday.builder()
                .ddayTitle("시험")
                .ddayDate(Date.valueOf("2024-09-14"))
                .user(user)
                .build());

        //when
        final List<Dday> ddayList = ddayRepository.findByUserOrderByDdayDateDesc(user);

        //then
        assertThat(ddayList).isNotNull();
        assertThat(ddayList).hasSize(2);
        assertThat(ddayList.get(0).getDdayTitle()).isEqualTo("시험");
    }

    @Nested
    class 디데이삭제 {
        @Test
        void 실패_나의디데이가아님() {
            //given
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(Date.valueOf("2023-02-09"))
                    .user(user)
                    .build();
            final Dday saveDday = ddayRepository.save(dday);
            final User other = user = userRepository.save(User.builder()
                    .email("aa@naver.com")
                    .password("123456")
                    .socialLogin(SocialType.BASIC)
                    .nickname("다른사람")
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .withdrawal(false)
                    .build());

            //when
            ddayRepository.deleteByUserAndId(other, saveDday.getId());
            final Dday checkDday = ddayRepository.findById(saveDday.getId()).orElse(null);

            //then
            assertThat(checkDday).isNotNull();
        }

        @Test
        void 실패_없는디데이ID() {
            //given
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(Date.valueOf("2023-02-09"))
                    .user(user)
                    .build();
            final Dday saveDday = ddayRepository.save(dday);

            //when
            ddayRepository.deleteByUserAndId(user, Long.MAX_VALUE);

            //then
        }

        @Test
        void 성공() {
            //given
            final Dday dday = Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(Date.valueOf("2023-02-09"))
                    .user(user)
                    .build();
            final Dday saveDday = ddayRepository.save(dday);

            //when
            ddayRepository.deleteByUserAndId(user, saveDday.getId());
            final Dday checkDday = ddayRepository.findById(saveDday.getId()).orElse(null);

            //then
            assertThat(checkDday).isNull();
        }
    }


    @Test
    void 디데이수정() {
        //given
        final Dday dday = Dday.builder()
                .ddayTitle("생일")
                .ddayDate(Date.valueOf("2023-02-09"))
                .user(user)
                .build();
        final Dday saveDday = ddayRepository.save(dday);
        final Dday changeDday = Dday.builder()
                .id(saveDday.getId())
                .createTime(saveDday.getCreateTime())
                .ddayTitle("시험")
                .ddayDate(Date.valueOf("2023-02-11"))
                .user(user)
                .build();
        //when
        ddayRepository.save(changeDday);
        final Dday findDday = ddayRepository.findByUserAndId(user, saveDday.getId());

        //then
        assertThat(findDday).isNotNull();
        assertThat(findDday.getDdayTitle()).isEqualTo("시험");
        assertThat(findDday.getDdayDate()).isEqualTo(Date.valueOf("2023-02-11"));
    }


    @Nested
    class 디데이조회_오늘과가까운날짜 {
        final Date test = Date.valueOf("2023-02-09");
        final Date today = Date.valueOf(LocalDate.now());
        final Date christmas = Date.valueOf(LocalDate.of(LocalDate.now().getYear() + 1, 12, 25));

        @Test
        void 오늘미래과거_디데이데이터가있는경우() {
            //given
            ddayRepository.save(Dday.builder()
                    .ddayTitle("시험")
                    .ddayDate(test)
                    .user(user)
                    .build());
            ddayRepository.save(Dday.builder()
                    .ddayTitle("생일")
                    .ddayDate(today)
                    .user(user)
                    .build());
            ddayRepository.save(Dday.builder()
                    .ddayTitle("크리스마스")
                    .ddayDate(christmas)
                    .user(user)
                    .build());

            //when
            final Dday ddayFuture = ddayRepository.findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(user, today);
            final Dday ddayPast = ddayRepository.findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(user, today);

            //then
            assertThat(ddayFuture).isNotNull();
            assertThat(ddayFuture.getDdayDate()).isEqualTo(today);
            assertThat(ddayFuture.getDdayTitle()).isEqualTo("생일");
            assertThat(ddayPast).isNotNull();
            assertThat(ddayPast.getDdayDate()).isEqualTo(test);
            assertThat(ddayPast.getDdayTitle()).isEqualTo("시험");
        }

        @Test
        void 미래과거_디데이데이터가있는경우() {
            //given
            ddayRepository.save(Dday.builder()
                    .ddayTitle("시험")
                    .ddayDate(test)
                    .user(user)
                    .build());
            ddayRepository.save(Dday.builder()
                    .ddayTitle("크리스마스")
                    .ddayDate(christmas)
                    .user(user)
                    .build());

            //when
            final Dday ddayFuture = ddayRepository.findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(user, today);
            final Dday ddayPast = ddayRepository.findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(user, today);

            //then
            assertThat(ddayFuture).isNotNull();
            assertThat(ddayFuture.getDdayDate()).isEqualTo(christmas);
            assertThat(ddayFuture.getDdayTitle()).isEqualTo("크리스마스");
            assertThat(ddayPast).isNotNull();
            assertThat(ddayPast.getDdayDate()).isEqualTo(test);
            assertThat(ddayPast.getDdayTitle()).isEqualTo("시험");
        }

        @Test
        void 과거_디데이데이터가있는경우() {
            //given
            ddayRepository.save(Dday.builder()
                    .ddayTitle("시험")
                    .ddayDate(test)
                    .user(user)
                    .build());
            ddayRepository.save(Dday.builder()
                    .ddayTitle("기념")
                    .ddayDate(Date.valueOf("2020-01-27"))
                    .user(user)
                    .build());
            ddayRepository.save(Dday.builder()
                    .ddayTitle("새해")
                    .ddayDate(Date.valueOf("2023-01-01"))
                    .user(user)
                    .build());

            //when
            final Dday ddayFuture = ddayRepository.findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(user, today);
            final Dday ddayPast = ddayRepository.findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(user, today);

            //then
            assertThat(ddayFuture).isNull();
            assertThat(ddayPast).isNotNull();
            assertThat(ddayPast.getDdayDate()).isEqualTo(test);
            assertThat(ddayPast.getDdayTitle()).isEqualTo("시험");
        }

        @Test
        void 디데이데이터가없는경우() {
            //given

            //when
            final Dday ddayFuture = ddayRepository.findTopByUserAndDdayDateGreaterThanEqualOrderByDdayDateAsc(user, today);
            final Dday ddayPast = ddayRepository.findTopByUserAndDdayDateBeforeOrderByDdayDateDesc(user, today);

            //then
            assertThat(ddayFuture).isNull();
            assertThat(ddayPast).isNull();
        }
    }
}