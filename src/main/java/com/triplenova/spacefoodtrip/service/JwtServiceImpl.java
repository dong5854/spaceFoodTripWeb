package com.triplenova.spacefoodtrip.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.triplenova.spacefoodtrip.domain.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService{

    private static final String ACCESS_TOKEN_SUBJECT = "Access Token";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";

    private final ObjectMapper objectMapper;


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;
    @Value("${jwt.access.header}")
    private String accessHeader;

    public String createAccessToken(String email) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);


        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);

        String token = objectMapper.writeValueAsString(tokenMap);

        response.getWriter().write(token);
    }

    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    @Override
    public String extractUserEmail(String accessToken) {
        return JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(EMAIL_CLAIM).asString();
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) throws IOException, ServletException {
        return Optional.ofNullable(request.getHeader(accessHeader)).filter(
                accessToken -> accessToken.startsWith(BEARER)
        ).map(accessToken -> accessToken.replace(BEARER, ""));
    }

    @Override
    public boolean isValid(String token){
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        }catch (Exception e){
            log.error("유효하지 않은 Token입니다", e.getMessage());
            return false;
        }
    }
}
