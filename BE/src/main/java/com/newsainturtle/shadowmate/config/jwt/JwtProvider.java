package com.newsainturtle.shadowmate.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${shadowmate.jwt.secret}")
    private String SECRETKEY;

    @Value("${shadowmate.jwt.expires}")
    private long EXPIRES;

    @Value("${shadowmate.jwt.header}")
    private String HEADER;

    @Value("${shadowmate.jwt.prefix}")
    private String PREFIX;

    public String createToken(PrincipalDetails principalDetails) {
        return JWT.create()
                .withSubject("ShadowMate 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRES))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("email", principalDetails.getUser().getEmail())
                .withClaim("socialType", principalDetails.getUser().getSocialLogin().toString())
                .sign(Algorithm.HMAC512(SECRETKEY));
    }

    public void addTokenHeader(HttpServletResponse response, String jwtToken) {
        StringBuilder sb = new StringBuilder();
        response.addHeader(HEADER,sb.append(PREFIX).append(jwtToken).toString());
    }

    public boolean validateHeader(HttpServletRequest request) {
        String jwtHeader = getHeader(request);
        if (jwtHeader == null || !jwtHeader.startsWith(PREFIX)) {
            request.setAttribute("exception",AuthErrorResult.FAIL_VALIDATE_TOKEN);
            throw new AuthException(AuthErrorResult.FAIL_VALIDATE_TOKEN);
        }
        return true;
    }

    public String validateToken(HttpServletRequest request) {
        String jwtToken = getToken(getHeader(request));
        try {
            return JWT.require(Algorithm.HMAC512(SECRETKEY))
                    .build()
                    .verify(jwtToken)
                    .getClaim("email")
                    .asString();
        }
        catch (Exception e){
            request.setAttribute("exception",AuthErrorResult.FAIL_VALIDATE_TOKEN);
            throw new AuthException(AuthErrorResult.FAIL_VALIDATE_TOKEN);
        }
    }

    public String validateSocialType(HttpServletRequest request) {
        String jwtToken = getToken(getHeader(request));
        try {
            return JWT.require(Algorithm.HMAC512(SECRETKEY))
                    .build()
                    .verify(jwtToken)
                    .getClaim("socialType")
                    .asString();
        }
        catch (Exception e){
            request.setAttribute("exception",AuthErrorResult.FAIL_VALIDATE_TOKEN_SOCIAL_TYPE);
            throw new AuthException(AuthErrorResult.FAIL_VALIDATE_TOKEN_SOCIAL_TYPE);
        }
    }

    private String getHeader(HttpServletRequest request) {
        return request.getHeader(HEADER);
    }

    private String getToken(String header) {
        return header.replace(PREFIX, "");
    }

}
