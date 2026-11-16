package com.epam.rd.autocode.assessment.appliances.exception;

import com.epam.rd.autocode.assessment.appliances.aspect.SecurityLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoggingAccessDeniedHandler implements AccessDeniedHandler {

    private final AccessDeniedHandler delegate = new AccessDeniedHandlerImpl();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exception) throws IOException, ServletException {

        SecurityLog.accessDenied(SecurityLog.currentUser(), request.getMethod(),
                request.getRequestURI(), exception.getClass().getSimpleName());
        delegate.handle(request, response, exception);
    }
}
