package com.newsainturtle.shadowmate.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsainturtle.shadowmate.common.util.JwtUtil;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Value("${shadowmate.jwt.secret}")
    private String secret;

    @Value("${shadowmate.jwt.expires}")
    private long expires;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper om = new ObjectMapper();
        try {
            User user = om.readValue(request.getInputStream(), User.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        String jwtToken = JWT.create()
                .withSubject("ShadowMate 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + expires))
                .withClaim("id", principalDetails.getUser().getUserId())
                .withClaim("email", principalDetails.getUser().getEmail())
                .sign(Algorithm.HMAC512(secret));

        response.addHeader(JwtUtil.HEADER_STRING, JwtUtil.TOKEN_PREFIX + jwtToken);
    }

}
