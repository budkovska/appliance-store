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

    @Around("@annotation(com.epam.rd.autocode.assessment.appliances.aspect.Loggable)")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();

        long start = System.currentTimeMillis();
        log.info("CALL   | {} with arguments: {}", methodName, Arrays.toString(args));

        try {
            Object result = pjp.proceed();
            long executionTime = System.currentTimeMillis() - start;
            log.info("SUCCESS | {} finished in {} ms", methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - start;
            log.error("ERROR   | {} failed after {} ms. Reason: {}", methodName, executionTime, e.getMessage());
            throw e;
        }
    }
}