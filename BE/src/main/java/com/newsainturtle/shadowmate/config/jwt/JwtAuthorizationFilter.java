package com.newsainturtle.shadowmate.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.newsainturtle.shadowmate.common.util.JwtUtil;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;

    @Value("${shadowmate.jwt.secret}")
    private String secret;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwtHeader = request.getHeader(JwtUtil.HEADER_STRING);
        if (jwtHeader == null || !jwtHeader.startsWith(JwtUtil.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = jwtHeader.replace(JwtUtil.TOKEN_PREFIX, "");
        String email = JWT.require(Algorithm.HMAC512(secret))
                .build()
                .verify(jwtToken)
                .getClaim("email")
                .asString();

        if (email != null) {
            User userEntity = userRepository.findByEmail(email);

            PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }
}
