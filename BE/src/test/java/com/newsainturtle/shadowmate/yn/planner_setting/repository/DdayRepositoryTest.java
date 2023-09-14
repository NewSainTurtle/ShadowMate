package com.newsainturtle.shadowmate.yn.planner_setting.repository;

import com.newsainturtle.shadowmate.planner_setting.entity.Dday;
import com.newsainturtle.shadowmate.planner_setting.repository.DdayRepository;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DdayRepositoryTest {

    @Autowired
    private DdayRepository ddayRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void init() {
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
    public void 디데이등록() {
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
    public void 디데이조회_미래순() {
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
        assertThat(ddayList.size()).isEqualTo(2);
        assertThat(ddayList.get(0).getDdayTitle()).isEqualTo("시험");
    }

    @Test
    public void 디데이삭제() {
        //given
        final Dday dday = Dday.builder()
                .ddayTitle("생일")
                .ddayDate(Date.valueOf("2023-02-09"))
                .user(user)
                .build();
        final Dday saveDday = ddayRepository.save(dday);

        //when
        ddayRepository.deleteById(saveDday.getId());
        final Dday checkDday = ddayRepository.findById(saveDday.getId()).orElse(null);

        //then
        assertThat(checkDday).isNull();
    }
}