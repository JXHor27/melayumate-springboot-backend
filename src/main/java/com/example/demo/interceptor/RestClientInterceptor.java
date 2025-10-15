package com.example.demo.interceptor;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestClientInterceptor implements ClientHttpRequestInterceptor {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_KEY = "traceId";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 1. Get the traceId from the MDC
        String traceId = MDC.get(TRACE_ID_KEY);

        // 2. If it exists, add it to the outgoing request's headers
        if (traceId != null) {
            request.getHeaders().add(TRACE_ID_HEADER, traceId);
        }

        // 3. Proceed with sending the request
        return execution.execute(request, body);
    }
}
