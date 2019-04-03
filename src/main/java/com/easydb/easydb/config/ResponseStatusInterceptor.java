package com.easydb.easydb.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ResponseStatusInterceptor implements HandlerInterceptor {

    private final ApplicationMetrics applicationMetrics;

    ResponseStatusInterceptor(ApplicationMetrics applicationMetrics) {
        this.applicationMetrics = applicationMetrics;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HttpStatus status = HttpStatus.valueOf(response.getStatus());
        if (status.is2xxSuccessful()) {
            applicationMetrics.application2xxRequests().increment();
        } else if (status.is4xxClientError()) {
            applicationMetrics.application4xxRequests().increment();
        } else if (status.is5xxServerError()) {
            applicationMetrics.application5xxRequests().increment();
        }
        applicationMetrics.applicationAllRequests().increment();
    }
}
