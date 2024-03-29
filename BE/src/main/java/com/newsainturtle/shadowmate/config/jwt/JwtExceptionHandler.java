package com.newsainturtle.shadowmate.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.common.ErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.newsainturtle.shadowmate.config.constant.ConfigConstant.KEY_EXCEPTION;

@Component
public class JwtExceptionHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        AuthErrorResult exception = (AuthErrorResult) request.getAttribute(KEY_EXCEPTION);
        setResponse(response,exception);
    }

    private void setResponse(HttpServletResponse response, AuthErrorResult authErrorResult) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(authErrorResult.getHttpStatus().value());
        ErrorResponse errorResponse = new ErrorResponse(authErrorResult.name(), authErrorResult.getMessage());
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
