package com.epam.rd.autocode.assessment.appliances.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                user("phobos@gmail.com", "111", "EMPLOYEE", encoder),
                user("deimos@gmail.com", "333", "EMPLOYEE", encoder),
                user("mercury@gmail.com", "111", "CLIENT", encoder),
                user("venus@gmail.com", "222", "CLIENT", encoder));
    }

    private UserDetails user(String email, String password, String role, PasswordEncoder encoder) {
        return org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(encoder.encode(password))
                .roles(role)
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(antMatcher("/"), antMatcher("/styles/**")).permitAll()
                        .requestMatchers(antMatcher("/clients/**"), antMatcher("/employees/**"),
                                antMatcher("/manufacturers/**")).hasRole("EMPLOYEE")
                        .requestMatchers(antMatcher("/appliances/add"), antMatcher("/appliances/add-appliance"),
                                antMatcher("/appliances/*/edit"), antMatcher("/appliances/*/delete"))
                        .hasRole("EMPLOYEE")
                        .requestMatchers(antMatcher("/orders/*/approved"), antMatcher("/orders/*/unapproved"))
                        .hasRole("EMPLOYEE")
                        .anyRequest().authenticated())
                .formLogin(form -> form.defaultSuccessUrl("/", true).permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(h -> h.frameOptions(f -> f.sameOrigin()));
        return http.build();
    }
}