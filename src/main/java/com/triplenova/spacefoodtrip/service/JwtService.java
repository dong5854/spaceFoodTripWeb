package com.triplenova.spacefoodtrip.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

public interface JwtService {

    String createAccessToken(String username);
    void sendAccessToken(HttpServletResponse response, String accessToken) throws IOException;
    void setAccessTokenHeader(HttpServletResponse response, String accessToken);
    String extractUserEmail(String accessToken);
    Optional<String> extractAccessToken(HttpServletRequest request) throws IOException, ServletException;
    boolean isValid(String token);
}
