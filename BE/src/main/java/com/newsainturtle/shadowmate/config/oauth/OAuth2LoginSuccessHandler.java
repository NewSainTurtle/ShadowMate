package com.newsainturtle.shadowmate.config.oauth;

import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(determineTargetUrl(request, response, authentication));
        request.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        requestDispatcher.forward(request,response);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String tartget = "/api/auth/social-login";
        return UriComponentsBuilder.fromUriString(tartget)
                .queryParam("token", response.getHeader("Authorization"))
                .build().toUriString();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String jwtToken = jwtProvider.createToken(principalDetails);
        jwtProvider.addTokenHeader(response, jwtToken);
        this.handle(request,response,authentication);
    }
}
