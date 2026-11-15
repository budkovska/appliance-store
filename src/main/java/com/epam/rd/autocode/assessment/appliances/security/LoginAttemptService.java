package com.epam.rd.autocode.assessment.appliances.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private record State(int failures, Instant lockedUntil) {
    }

    private final Map<String, State> states = new ConcurrentHashMap<>();
    private final int maxFailures;
    private final Duration lockDuration;

    public LoginAttemptService(@Value("${app.security.max-failed-attempts}") int maxFailures,
                               @Value("${app.security.lock-minutes}") long lockMinutes) {
        this.maxFailures = maxFailures;
        this.lockDuration = Duration.ofMinutes(lockMinutes);
    }

    public boolean isLocked(String email) {
        String key = key(email);
        State state = states.get(key);
        if (state == null || state.lockedUntil() == null) {
            return false;
        }
        if (Instant.now().isAfter(state.lockedUntil())) {
            states.remove(key);
            return false;
        }
        return true;
    }

    public void loginFailed(String email) {
        states.compute(key(email), (k, old) -> {
            int failures = (old == null) ? 1 : old.failures() + 1;
            if (failures >= maxFailures) {
                return new State(0, Instant.now().plus(lockDuration));
            }
            return new State(failures, null);
        });
    }

    public void loginSucceeded(String email) {
        states.remove(key(email));
    }

    private String key(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}