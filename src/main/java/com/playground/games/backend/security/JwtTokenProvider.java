package com.playground.games.backend.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

public interface JwtTokenProvider {
    String generateToken(String username);

    UsernamePasswordAuthenticationToken getAuthentication(String token);

    String resolveToken(HttpServletRequest request);

    boolean validateToken(String token);
}