package com.newsainturtle.shadowmate.yn.planner.repository;

import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 탈퇴하지않은사용자조회() {
        //given
        final User user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(false)
                .build());

        //when
        final User findUser = userRepository.findByIdAndWithdrawalIsFalse(user.getId());

        //then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(findUser.getSocialLogin()).isEqualTo(user.getSocialLogin());
        assertThat(findUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(findUser.getPlannerAccessScope()).isEqualTo(user.getPlannerAccessScope());
        assertThat(findUser.getWithdrawal()).isEqualTo(user.getWithdrawal());
    }

    @Test
    public void 탈퇴하지않은사용자조회_탈퇴한경우() {
        //given
        final User user = userRepository.save(User.builder()
                .email("test1234@naver.com")
                .password("123456")
                .socialLogin(SocialType.BASIC)
                .nickname("거북이")
                .plannerAccessScope(PlannerAccessScope.PUBLIC)
                .withdrawal(true)
                .build());

        //when
        final User findUser = userRepository.findByIdAndWithdrawalIsFalse(user.getId());

        //then
        assertThat(findUser).isNull();
    }
    
}
