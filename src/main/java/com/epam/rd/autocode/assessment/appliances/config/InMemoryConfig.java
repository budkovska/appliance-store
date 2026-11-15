package com.epam.rd.autocode.assessment.appliances.config;
import com.epam.rd.autocode.assessment.appliances.security.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@Profile("inmemory")
public class InMemoryConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                user("phobos@gmail.com", "111", Role.EMPLOYEE, encoder),
                user("deimos@gmail.com", "333", Role.EMPLOYEE, encoder),
                user("mercury@gmail.com", "111", Role.CLIENT, encoder),
                user("venus@gmail.com", "222", Role.CLIENT, encoder));
    }

    private UserDetails user(String email, String password, Role role, PasswordEncoder encoder) {
        return User.withUsername(email)
                .password(encoder.encode(password))
                .roles(role.name())
                .build();
    }
}
