package com.example.demo.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1) // Ensure this filter runs first
public class MDCLoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. GET or GENERATE the traceId
        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            // Generate a new traceId if one is not provided in the header
            traceId = UUID.randomUUID().toString();
        }

        try {
            // 2. PUT the traceId into the MDC
            MDC.put(TRACE_ID_KEY, traceId);

            // Also, add it to the response header so the client or next service can see it
            response.addHeader(TRACE_ID_HEADER, traceId);

            // 3. PROCEED with the filter chain
            filterChain.doFilter(request, response);

        } finally {
            // 4. CLEAR the MDC context after the request is complete
            // This is crucial to prevent memory leaks and incorrect traceIds on reused threads
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
