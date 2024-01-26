package com.newsainturtle.shadowmate.config.oauth;

import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.newsainturtle.shadowmate.config.constant.ConfigConstant.*;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetURL = "https://shadowmate.kro.kr/login";
        request.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        getRedirectStrategy().sendRedirect(request, response, targetURL);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String target = "/api/auth/social-login";
        return UriComponentsBuilder.fromUriString(target)
                .queryParam(KEY_TOKEN, response.getHeader("Authorization"))
                .build().toUriString();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String jwtToken = jwtProvider.createToken(principalDetails, principalDetails.getAttribute("sub"));
        response.addCookie(addCookie(KEY_USER_ID, principalDetails.getUser().getId().toString()));
        response.addCookie(addCookie(KEY_TOKEN, jwtToken));
        response.addCookie(addCookie(KEY_TYPE, principalDetails.getAttribute("sub")));
        this.handle(request, response, authentication);
    }

    private Cookie addCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(100000);
        return cookie;
    }
}
