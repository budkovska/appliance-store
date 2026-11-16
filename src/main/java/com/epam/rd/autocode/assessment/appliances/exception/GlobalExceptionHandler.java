package com.epam.rd.autocode.assessment.appliances.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView notFound(ResourceNotFoundException e, HttpServletRequest request) {
        log.warn("Not found: {} {} | {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return page(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView accessDenied(AccessDeniedException e, HttpServletRequest request) {
        log.warn("SECURITY | Access denied: user={} {} {}",
                request.getRemoteUser(), request.getMethod(), request.getRequestURI());
        return page(HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ResourceInUseException.class, DuplicateEmailException.class,
            SelfDeletionException.class, OrderLockedException.class})
    public ModelAndView conflict(RuntimeException e, HttpServletRequest request) {
        log.warn("Conflict: {} {} | {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return page(HttpStatus.CONFLICT);
    }

    @ExceptionHandler({InvalidQuantityException.class, IllegalArgumentException.class, TypeMismatchException.class})
    public ModelAndView badRequest(Exception e, HttpServletRequest request) {
        log.warn("Bad request: {} {} | {}", request.getMethod(), request.getRequestURI(), e.getMessage());
        return page(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView other(Exception e, HttpServletRequest request) {
        if (e instanceof ErrorResponse errorResponse) {
            HttpStatus status = HttpStatus.resolve(errorResponse.getStatusCode().value());
            if (status != null && status.is4xxClientError()) {
                log.debug("Client error {}: {} {}", status.value(), request.getMethod(), request.getRequestURI());
                return page(status);
            }
        }
        log.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), e);
        return page(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ModelAndView page(HttpStatus status) {
        String view = switch (status) {
            case FORBIDDEN -> "error/403";
            case NOT_FOUND -> "error/404";
            case INTERNAL_SERVER_ERROR -> "error/500";
            default -> "error/4xx";
        };
        ModelAndView mav = new ModelAndView(view);
        mav.setStatus(status);
        mav.addObject("status", status.value());
        return mav;
    }
}