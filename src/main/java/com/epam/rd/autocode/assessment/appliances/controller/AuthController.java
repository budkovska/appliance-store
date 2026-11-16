package com.epam.rd.autocode.assessment.appliances.controller;

import com.epam.rd.autocode.assessment.appliances.aspect.SecurityLog;
import com.epam.rd.autocode.assessment.appliances.dto.RegistrationDto;
import com.epam.rd.autocode.assessment.appliances.exception.DuplicateEmailException;
import com.epam.rd.autocode.assessment.appliances.security.JwtCookieService;
import com.epam.rd.autocode.assessment.appliances.security.JwtService;
import com.epam.rd.autocode.assessment.appliances.security.LoginAttemptService;
import com.epam.rd.autocode.assessment.appliances.service.ClientService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtCookieService cookieService;
    private final LoginAttemptService loginAttempts;
    private final ClientService clientService;

    @GetMapping("/login")
    public String loginPage(Authentication auth) {
        if (isLoggedIn(auth)) {
            return "redirect:/";
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response) {

        if (loginAttempts.isLocked(email)) {
            SecurityLog.accountLocked(email);
            return "redirect:/login?locked";
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            loginAttempts.loginSucceeded(email);
            cookieService.addToken(response, jwtService.generateToken(auth.getName()));
            SecurityLog.loginSuccess(auth.getName());
            return "redirect:/";

        } catch (AuthenticationException e) {
            loginAttempts.loginFailed(email);
            SecurityLog.loginFailed(email);
            return "redirect:/login?error";
        }
    }


    @InitBinder("registration")
    public void disallowId(WebDataBinder binder) {
        binder.setDisallowedFields("id");
    }

    @GetMapping("/register")
    public String registerPage(Authentication auth, Model model) {
        if (isLoggedIn(auth)) {
            return "redirect:/";
        }
        model.addAttribute("registration", new RegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registration") RegistrationDto registration,
                           BindingResult result) {

        if (!StringUtils.hasText(registration.getPassword())) {
            result.rejectValue("password", "validation.required");
        } else if (!registration.getPassword().equals(registration.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "validation.password.mismatch");
        }
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            clientService.register(registration);
        } catch (DuplicateEmailException e) {
            result.rejectValue("email", "validation.email.unique");
            return "auth/register";
        }
        SecurityLog.registered(registration.getEmail());
        return "redirect:/login?registered";
    }

    private boolean isLoggedIn(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }
}