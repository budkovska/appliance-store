package com.epam.rd.autocode.assessment.appliances.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import java.util.Optional;

@Component
public class JwtCookieService {

    private final String cookieName;
    private final boolean secure;
    private final Duration maxAge;

    public JwtCookieService(@Value("${app.jwt.cookie-name}") String cookieName,
                            @Value("${app.jwt.cookie-secure}") boolean secure,
                            JwtService jwtService) {
        this.cookieName = cookieName;
        this.secure = secure;
        this.maxAge = jwtService.getLifetime();
    }

    public void addToken(HttpServletResponse response, String token) {
        response.addHeader(HttpHeaders.SET_COOKIE, build(token, maxAge).toString());
    }

    public void clear(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, build("", Duration.ZERO).toString());
    }

    public Optional<String> readToken(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null || cookie.getValue().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(cookie.getValue());
    }

    private ResponseCookie build(String value, Duration age) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(age)
                .build();
    }
}