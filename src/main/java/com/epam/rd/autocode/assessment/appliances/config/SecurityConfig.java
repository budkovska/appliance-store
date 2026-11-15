package com.epam.rd.autocode.assessment.appliances.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           @Value("${spring.h2.console.enabled:false}") boolean h2ConsoleEnabled) throws Exception {
        http
                .authorizeHttpRequests(auth -> {
                    if (h2ConsoleEnabled) {
                        auth.requestMatchers(antMatcher("/h2-console/**")).permitAll();
                    }
                    auth
                            .requestMatchers(antMatcher("/"), antMatcher("/styles/**")).permitAll()
                            .requestMatchers(antMatcher("/clients/**"), antMatcher("/employees/**"),
                                    antMatcher("/manufacturers/**")).hasRole("EMPLOYEE")
                            .requestMatchers(antMatcher("/appliances/add"), antMatcher("/appliances/add-appliance"),
                                    antMatcher("/appliances/*/edit"), antMatcher("/appliances/*/delete"))
                            .hasRole("EMPLOYEE")
                            .requestMatchers(antMatcher("/orders/*/approved"), antMatcher("/orders/*/unapproved"))
                            .hasRole("EMPLOYEE")
                            .anyRequest().authenticated();
                })
                .formLogin(form -> form.defaultSuccessUrl("/", true).permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .csrf(csrf -> {
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