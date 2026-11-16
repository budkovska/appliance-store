package com.epam.rd.autocode.assessment.appliances.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingServices {

    private static final String EXCEPTION_PACKAGE = "com.epam.rd.autocode.assessment.appliances.exception";
    private static final int MAX_ARGS_LENGTH = 300;

    @Around("@annotation(com.epam.rd.autocode.assessment.appliances.aspect.Loggable)")
    public Object logChange(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().toShortString();
        String args = formatArgs(pjp.getArgs());
        String user = SecurityLog.currentUser();
        long start = System.nanoTime();

        log.debug("CALL     | user={} | {} | args={}", user, method, args);
        try {
            Object result = pjp.proceed();
            log.info("SUCCESS  | user={} | {} | {} ms | args={}", user, method, elapsedMs(start), args);
            return result;
        } catch (RuntimeException e) {
            if (e.getClass().getPackageName().equals(EXCEPTION_PACKAGE)) {
                log.warn("REJECTED | user={} | {} | {} ms | {}: {}", user, method, elapsedMs(start),
                        e.getClass().getSimpleName(), LogSanitizer.clean(e.getMessage(), 200));
            }
            throw e;
        }
    }

    @Around("execution(* com.epam.rd.autocode.assessment.appliances.service.impl..*(..))"
            + " && !@annotation(com.epam.rd.autocode.assessment.appliances.aspect.Loggable)")
    public Object logRead(ProceedingJoinPoint pjp) throws Throwable {
        if (!log.isDebugEnabled()) {
            return pjp.proceed();
        }
        String method = pjp.getSignature().toShortString();
        long start = System.nanoTime();

        log.debug("CALL     | {} | args={}", method, formatArgs(pjp.getArgs()));
        Object result = pjp.proceed();
        log.debug("RETURN   | {} | {} ms", method, elapsedMs(start));
        return result;
    }

    private String formatArgs(Object[] args) {
        return LogSanitizer.clean(Arrays.toString(args), MAX_ARGS_LENGTH);
    }

    private long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}