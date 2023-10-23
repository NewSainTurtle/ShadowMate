package com.newsainturtle.shadowmate.config;

import com.newsainturtle.shadowmate.config.jwt.JwtException;
import com.newsainturtle.shadowmate.config.jwt.JwtAuthenticationFilter;
import com.newsainturtle.shadowmate.config.jwt.JwtAuthorizationFilter;
import com.newsainturtle.shadowmate.config.jwt.JwtProvider;
import com.newsainturtle.shadowmate.config.oauth.OAuth2LoginSuccessHandler;
import com.newsainturtle.shadowmate.config.oauth.PrincipalOauth2UserService;
import com.newsainturtle.shadowmate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;

    private final UserRepository userRepository;

    private final JwtProvider jwtProvider;

    private final JwtException jwtException;

    private final PrincipalOauth2UserService principalOauth2UserService;

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    private static final String[] PERMIT_ALL_URL_ARRAY = {
            "/api/auth/**",
            "/api/oauth/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new CustomFilter())
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtException)
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/api/oauth/")
                .and()
                .userInfoEndpoint().userService(principalOauth2UserService)
                .and()
                .successHandler(oAuth2LoginSuccessHandler)
                .and()
                .authorizeRequests(authorize -> authorize
                        .antMatchers(PERMIT_ALL_URL_ARRAY).permitAll()
                        .anyRequest().authenticated())
                ;

        return http.build();
    }

    public class CustomFilter extends AbstractHttpConfigurer<CustomFilter, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager,jwtProvider);
            jwtAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");
            http.addFilter(corsFilter)
                    .addFilter(jwtAuthenticationFilter)
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, userRepository, jwtProvider));
        }
    }
}