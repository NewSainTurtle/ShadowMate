package com.newsainturtle.shadowmate.config.oauth;

import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
                .queryParam("token", response.getHeader("Authorization"))
                .build().toUriString();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String jwtToken = jwtProvider.createToken(principalDetails);
        response.addCookie(addCookie("userId", principalDetails.getUser().getId().toString()));
        response.addCookie(addCookie("token", jwtToken));
        this.handle(request,response,authentication);
    }

    private Cookie addCookie(String Key, String value) {
        Cookie cookie = new Cookie(Key, value);
        cookie.setPath("/");
        cookie.setMaxAge(100000);
        return cookie;
    }
}
