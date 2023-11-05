package com.newsainturtle.shadowmate.kh.auth;

import com.newsainturtle.shadowmate.auth.dto.JoinRequest;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    //이메일 인증 테스트(+중복검사)
    // 1. 이메일 중복검사
    // - 이메일 중복된 경우
    // - 이메일 중복되지 않은 경우

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final User user = User.builder()
            .email("test1@test.com")
            .password("123456")
            .socialLogin(SocialType.BASIC)
            .nickname("거북이1")
            .withdrawal(false)
            .plannerAccessScope(PlannerAccessScope.PUBLIC)
            .build();

    @BeforeEach
    public void init() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }


    @Nested
    class 이메일인증 {

        @Test
        void 실패_이메일이_중복된_경우() {
            //given
            userRepository.save(user);

            final String email = "test1@test.com";

            //when
            final User result = userRepository.findByEmail(email);

            //then
            assertThat(result).isEqualTo(user).isNotNull();
        }
    }
    @Nested
    class 회원가입 {
        @Test
        void 성공_회원가입() {
            // given
            final JoinRequest joinRequest =
                    JoinRequest.builder()
                            .email("test@test.com")
                            .password("123456")
                            .nickname("닉네임임")
                            .build();
            final User userEntity =
                    User.builder()
                            .email(joinRequest.getEmail())
                            .password(bCryptPasswordEncoder.encode(joinRequest.getPassword()))
                            .socialLogin(SocialType.BASIC)
                            .nickname(joinRequest.getNickname())
                            .plannerAccessScope(PlannerAccessScope.PUBLIC)
                            .withdrawal(false)
                            .build();

            // when
            final User user = userRepository.save(userEntity);

            // then
            assertThat(user.getEmail()).isEqualTo(joinRequest.getEmail());
            assertThat(user.getNickname()).isEqualTo(joinRequest.getNickname());
            assertThat(bCryptPasswordEncoder.matches(
                    joinRequest.getPassword(), user.getPassword())).isTrue();
        }
    }

    @Nested
    class 닉네임인증 {
        @Test
        @DisplayName("닉네임이 중복된 경우")
        void 실패_닉네임중복() {
            //given
            userRepository.save(user);

            //when
            final User userEntity = userRepository.findByNickname(user.getNickname());

            //then
            assertThat(userEntity).isNotNull();
            assertThat(userEntity.getNickname()).isEqualTo(user.getNickname());
        }

        @Test
        @DisplayName("닉네임이 중복되지 않은 경우")
        void 성공_닉네임중복이아님() {
            // given

            // when
            final User userEntity = userRepository.findByNickname(user.getNickname());

            // then
            assertThat(userEntity).isNull();
        }
    }
}
