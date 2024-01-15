package com.newsainturtle.shadowmate.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsainturtle.shadowmate.auth.service.RedisService;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.user.entity.User;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;
    private RedisService redisService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider, RedisService redisService) {
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.redisService = redisService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
        String jwtToken = jwtProvider.createToken(principalDetails, request.getSession().getId());
        jwtProvider.addTokenHeader(response, jwtToken);
        response.setHeader("id", principalDetails.getUser().getId().toString());
        response.setHeader("type", request.getSession().getId());
        if (request.getHeader("Auto-Login") != null && request.getHeader("Auto-Login").equals("true")) {
            final String key = request.getSession().getId() + " " + principalDetails.getUser().getId().toString();
            response.setHeader("Auto-Login", key);
            redisService.setAutoLoginData(key, principalDetails.getUser().getId().toString());
        }
        chain.doFilter(request, response);
    }

}
