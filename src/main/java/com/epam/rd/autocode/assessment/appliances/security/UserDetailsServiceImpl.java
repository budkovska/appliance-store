package com.epam.rd.autocode.assessment.appliances.security;

import com.epam.rd.autocode.assessment.appliances.model.User;
import com.epam.rd.autocode.assessment.appliances.repository.ClientRepository;
import com.epam.rd.autocode.assessment.appliances.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Profile("!inmemory")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final EmployeeRepository employees;
    private final ClientRepository clients;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);
        return employees.findFirstByEmailIgnoreCase(email)
                .map(e -> toDetails(e, Role.EMPLOYEE))
                .or(() -> clients.findFirstByEmailIgnoreCase(email)
                        .map(c -> toDetails(c, Role.CLIENT)))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private UserDetails toDetails(User user, Role role) {
        return new AppUserDetails(user.getId(), user.getName(), user.getEmail(),
                user.getPassword(), role);
    }
}