package com.newsainturtle.shadowmate.config.jwt;

import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import com.newsainturtle.shadowmate.user.entity.User;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
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

    private JwtProvider jwtProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtProvider jwtProvider) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("JwtAuthorizationFilter.doFilterInternal");
        System.out.println("request = " + request.getHeader("Authorization"));
        System.out.println("response = " + response.getHeader("Authorization"));
        System.out.println(response);

        System.out.println("request Test = " + request.getParameter("token"));


        if(request.getRequestURI().equals("/api/auth/google")) {
            System.out.println(" = " + "여기");
            chain.doFilter(request, response);
            return;
            //super.doFilterInternal(request, response, chain);
        }
        else {
            System.out.println("?여긴?");
            if(!jwtProvider.validateHeader(request)) {
                System.out.println("?여긴?request");
                chain.doFilter(request, response);
                return;
            }
            String email = jwtProvider.validateToken(request);
            System.out.println("email = " + email);
            if (email != null) {
                User userEntity = userRepository.findByEmail(email);

                PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
            }
        }
        System.out.println("다시실행?");
    }
}
