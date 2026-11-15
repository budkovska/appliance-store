package com.epam.rd.autocode.assessment.appliances.config;

import com.epam.rd.autocode.assessment.appliances.security.JwtAuthenticationFilter;
import com.epam.rd.autocode.assessment.appliances.security.JwtCookieService;
import com.epam.rd.autocode.assessment.appliances.security.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtService jwtService,
                                           JwtCookieService cookieService,
                                           UserDetailsService userDetailsService,
                                           @Value("${spring.h2.console.enabled:false}") boolean h2ConsoleEnabled)
            throws Exception {

        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(jwtService, cookieService, userDetailsService);

        http
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> {
                    if (h2ConsoleEnabled) {
                        auth.requestMatchers(antMatcher("/h2-console/**")).permitAll();
                    }
                    auth
                            .requestMatchers(antMatcher("/"), antMatcher("/login"),
                                    antMatcher("/error"), antMatcher("/styles/**")).permitAll()
                            .requestMatchers(antMatcher("/clients/**"), antMatcher("/employees/**"),
                                    antMatcher("/manufacturers/**")).hasRole("EMPLOYEE")
                            .requestMatchers(antMatcher("/appliances/add"), antMatcher("/appliances/add-appliance"),
                                    antMatcher("/appliances/*/edit"), antMatcher("/appliances/*/delete"))
                            .hasRole("EMPLOYEE")
                            .requestMatchers(antMatcher("/orders/*/approved"), antMatcher("/orders/*/unapproved"))
                            .hasRole("EMPLOYEE")
                            .anyRequest().authenticated();
                })

                .exceptionHandling(e -> e.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))

                .addFilterAfter(jwtFilter, SessionManagementFilter.class)

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((request, response, authentication) -> {
                            String who = cookieService.readToken(request)
                                    .map(token -> {
                                        try {
                                            return jwtService.extractEmail(token);
                                        } catch (JwtException e) {
                                            return "unknown";
                                        }
                                    })
                                    .orElse("unknown");
                            cookieService.clear(response);
                            log.info("SECURITY | Logout: {}", who);
                        })
                        .logoutSuccessUrl("/"))

                .csrf(csrf -> {
                    csrf.csrfTokenRepository(new CookieCsrfTokenRepository());
                    if (h2ConsoleEnabled) {
                        csrf.ignoringRequestMatchers(antMatcher("/h2-console/**"));
                    }
                })
                .headers(headers -> {
                    if (h2ConsoleEnabled) {
                        headers.frameOptions(frame -> frame.sameOrigin());
                    }
                });

        return http.build();
    }
}