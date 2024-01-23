package com.newsainturtle.shadowmate.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.newsainturtle.shadowmate.auth.exception.AuthErrorResult;
import com.newsainturtle.shadowmate.auth.exception.AuthException;
import com.newsainturtle.shadowmate.auth.service.RedisService;
import com.newsainturtle.shadowmate.config.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${shadowmate.jwt.secret}")
    private String secretKey;

    @Value("${shadowmate.jwt.access.expires}")
    private long accessExpires;

    @Value("${shadowmate.jwt.refresh.expires}")
    private long refreshExpires;

    @Value("${shadowmate.jwt.header}")
    private String header;

    @Value("${shadowmate.jwt.prefix}")
    private String prefix;

    private final RedisService redisServiceImpl;

    public String createToken(final PrincipalDetails principalDetails, final String type) {
        createRefreshToken(principalDetails, type);
        return createAccessToken(principalDetails);
    }

    public String createAccessToken(final PrincipalDetails principalDetails) {
        return JWT.create()
                .withSubject("ShadowMate 액세스 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + accessExpires))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("email", principalDetails.getUser().getEmail())
                .withClaim("socialType", principalDetails.getUser().getSocialLogin().toString())
                .sign(Algorithm.HMAC512(secretKey));
    }

    public void createRefreshToken(final PrincipalDetails principalDetails, final String type) {
        final String refreshToken = JWT.create()
                .withSubject("ShadowMate 리프레시 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshExpires))
                .withClaim("id", principalDetails.getUser().getId())
                .withClaim("email", principalDetails.getUser().getEmail())
                .withClaim("socialType", principalDetails.getUser().getSocialLogin().toString())
                .sign(Algorithm.HMAC512(secretKey));
        redisServiceImpl.setRefreshTokenData(principalDetails.getUser().getId(), type, refreshToken, (int) refreshExpires);
    }

    public void addTokenHeader(HttpServletResponse response, String jwtToken) {
        StringBuilder sb = new StringBuilder();
        response.addHeader(header, sb.append(prefix).append(jwtToken).toString());
    }

    public boolean validateHeader(HttpServletRequest request) {
        String jwtHeader = getHeader(request);
        if (jwtHeader == null || !jwtHeader.startsWith(prefix)) {
            request.setAttribute("exception", AuthErrorResult.FAIL_VALIDATE_TOKEN);
            throw new AuthException(AuthErrorResult.FAIL_VALIDATE_TOKEN);
        }
        return true;
    }

    public String validateToken(HttpServletRequest request) {
        final String jwtToken = getToken(getHeader(request));
        try {
            if (JWT.decode(jwtToken).getExpiresAt().before(new Date())) {
                throw new AuthException(AuthErrorResult.EXPIRED_ACCESS_TOKEN);
            }
            return JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(jwtToken)
                    .getClaim("email")
                    .asString();
        } catch (AuthException e) {
            request.setAttribute("exception", AuthErrorResult.EXPIRED_ACCESS_TOKEN);
            throw new AuthException(AuthErrorResult.EXPIRED_ACCESS_TOKEN);
        } catch (Exception e) {
            request.setAttribute("exception", AuthErrorResult.FAIL_VALIDATE_TOKEN);
            throw new AuthException(AuthErrorResult.FAIL_VALIDATE_TOKEN);
        }
    }

    public String validateSocialType(HttpServletRequest request) {
        final String jwtToken = getToken(getHeader(request));
        try {
            if (JWT.decode(jwtToken).getExpiresAt().before(new Date())) {
                throw new AuthException(AuthErrorResult.EXPIRED_ACCESS_TOKEN);
            }

            return JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(jwtToken)
                    .getClaim("socialType")
                    .asString();
        } catch (AuthException e) {
            request.setAttribute("exception", AuthErrorResult.EXPIRED_ACCESS_TOKEN);
            throw new AuthException(AuthErrorResult.EXPIRED_ACCESS_TOKEN);
        } catch (Exception e) {
            request.setAttribute("exception", AuthErrorResult.FAIL_VALIDATE_TOKEN_SOCIAL_TYPE);
            throw new AuthException(AuthErrorResult.FAIL_VALIDATE_TOKEN_SOCIAL_TYPE);
        }
    }

    private String getHeader(HttpServletRequest request) {
        return request.getHeader(header);
    }

    private String getToken(String header) {
        return header.replace(prefix, "");
    }

}
