package com.resilientmesh.orders.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Random;

@RestController
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final Random random = new Random();

    @Value("${service.name:${SERVICE_NAME:orders-service}}")
    private String serviceName;

    @Value("${chaos.probability:${CHAOS_PROBABILITY:0.3}}")
    private double chaosProbability;

    @Value("${chaos.maxExtraLatencyMs:${MAX_EXTRA_LATENCY_MS:1500}}")
    private int maxExtraLatencyMs;

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrder(@PathVariable String id) throws InterruptedException {
        maybeInjectLatency();
        if (shouldFail()) {
            log.warn("Chaos: failing request for order {} on {}", id, serviceName);
            return ResponseEntity.status(500).body(
                    new OrderResponse(id, serviceName, "ERROR", "Chaos-induced failure at " + Instant.now())
            );
        }
        log.info("Handling order {} on {}", id, serviceName);
        return ResponseEntity.ok(new OrderResponse(id, serviceName, "OK", "Processed at " + Instant.now()));
    }

    private void maybeInjectLatency() throws InterruptedException {
        if (maxExtraLatencyMs <= 0) return;
        int extra = random.nextInt(maxExtraLatencyMs);
        if (extra > 0 && random.nextDouble() < chaosProbability) {
            log.warn("Chaos: injecting extra latency {} ms on {}", extra, serviceName);
            Thread.sleep(extra);
        }
    }

    private boolean shouldFail() {
        return random.nextDouble() < chaosProbability;
    }

    public static class OrderResponse {
        public String id;
        public String handledBy;
        public String status;
        public String message;

        public OrderResponse(String id, String handledBy, String status, String message) {
            this.id = id;
            this.handledBy = handledBy;
            this.status = status;
            this.message = message;
        }
    }

}
