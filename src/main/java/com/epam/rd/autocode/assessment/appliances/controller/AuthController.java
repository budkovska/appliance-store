package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.security.JwtCookieService;
import com.epam.rd.autocode.assessment.appliances.security.JwtService;
import com.epam.rd.autocode.assessment.appliances.security.LoginAttemptService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtCookieService cookieService;
    private final LoginAttemptService loginAttempts;

    @GetMapping("/login")
    public String loginPage(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response) {

        if (loginAttempts.isLocked(email)) {
            log.warn("SECURITY | Login rejected, account temporarily locked: {}", email);
            return "redirect:/login?locked";
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            loginAttempts.loginSucceeded(email);
            cookieService.addToken(response, jwtService.generateToken(auth.getName()));
            log.info("SECURITY | Login success: {}", auth.getName());
            return "redirect:/";

        } catch (AuthenticationException e) {
            loginAttempts.loginFailed(email);
            log.warn("SECURITY | Login failed: {}", email);
            return "redirect:/login?error";
        }
    }
}