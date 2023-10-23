package com.newsainturtle.shadowmate.config.oauth;

import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.enums.PlannerAccessScope;
import com.newsainturtle.shadowmate.user.enums.SocialType;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String userEmail = oAuth2User.getAttribute("email");
        User user = findByEmailAndSocialLogin(userEmail);
        if(user == null) {
            user = User.builder()
                    .email(userEmail)
                    .password("INTP")
                    .nickname(createNicknameRandomCode(oAuth2User.getAttribute("name")))
                    .socialLogin(SocialType.GOOGLE)
                    .plannerAccessScope(PlannerAccessScope.PUBLIC)
                    .withdrawal(false)
                    .build();
            userRepository.save(user);
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    private User findByEmailAndSocialLogin(final String email) {
        return userRepository.findByEmailAndSocialLogin(email, SocialType.GOOGLE);
    }
    private String createNicknameRandomCode(String name) {
        final String temp = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder();
        sb.append(name);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            sb.append(temp.charAt(random.nextInt(temp.length())));
        }
        String nickname = sb.toString();
        User user = userRepository.findByNickname(nickname);
        if(user!=null) {
            nickname = createNicknameRandomCode(name);
        }

        return nickname;
    }
}
