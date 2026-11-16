package com.epam.rd.autocode.assessment.appliances.aspect;

import com.epam.rd.autocode.assessment.appliances.aspect.LogSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityLog {

    private static final Logger LOG = LoggerFactory.getLogger("SECURITY");
    private static final int MAX_LENGTH = 100;

    private SecurityLog() {
    }

    public static void loginSuccess(String email) {
        LOG.info("Login success: {}", clean(email));
    }

    public static void loginFailed(String email) {
        LOG.warn("Login failed: {}", clean(email));
    }

    public static void accountLocked(String email) {
        LOG.warn("Login rejected, account temporarily locked: {}", clean(email));
    }

    public static void lockedOut(String email, int attempts, long minutes) {
        LOG.warn("Account locked for {} min after {} failed attempts: {}", minutes, attempts, clean(email));
    }

    public static void logout(String email) {
        LOG.info("Logout: {}", clean(email));
    }

    public static void registered(String email) {
        LOG.info("New client registered: {}", clean(email));
    }

    public static void tokenRejected(String ip, String reason) {
        LOG.warn("Rejected JWT from {}: {}", clean(ip), clean(reason));
    }

    public static void accessDenied(String user, String method, String uri, String reason) {
        LOG.warn("Access denied: user={} {} {} ({})", clean(user), clean(method), clean(uri), clean(reason));
    }

    public static String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "anonymous" : clean(auth.getName());
    }

    private static String clean(String value) {
        return LogSanitizer.clean(value, MAX_LENGTH);
    }
}