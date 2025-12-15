package com.resilientmesh.gateway.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Component
public class OrderClient {

    private final List<String> baseUrls;
    private final RestTemplate restTemplate = new RestTemplate();

    public OrderClient(List<String> baseUrls) {
        this.baseUrls = baseUrls;
    }

    @CircuitBreaker(name = "orders", fallbackMethod = "fallback")
    @Retry(name = "orders")
    public ResponseEntity<String> getOrder(String id) {
        RuntimeException lastEx = null;
        for (String base : baseUrls) {
            String url = base + "/orders/" + id;
            try {
                ResponseEntity<String> resp = restTemplate.getForEntity(URI.create(url), String.class);
                if (resp.getStatusCode().is2xxSuccessful()) {
                    return resp;
                }
            } catch (RuntimeException ex) {
                lastEx = ex;
            }
        }
        if (lastEx != null) {
            throw lastEx;
        }
        throw new RuntimeException("No downstream instances available");
    }

    @SuppressWarnings("unused")
    public ResponseEntity<String> fallback(String id, Throwable t) {
        String body = String.format(
                "{ \"id\": \"%s\", \"status\": \"DEGRADED\", \"message\": \"Using fallback, all instances failing\" }",
                id
        );
        return ResponseEntity.status(503).body(body);
    }

}
