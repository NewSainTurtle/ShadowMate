package com.newsainturtle.shadowmate.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.common.ErrorResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            filterChain.doFilter(request, response);
        }
        catch (AuthException e) {
            response.setStatus(e.getErrorResult().getHttpStatus().value());
            response.setContentType("application/json;charset=UTF-8");
            ErrorResponse errorResponse = new ErrorResponse(e.getErrorResult().name(),e.getErrorResult().getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
