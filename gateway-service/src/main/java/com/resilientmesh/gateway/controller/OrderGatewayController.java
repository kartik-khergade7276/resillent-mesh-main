package com.resilientmesh.gateway.controller;

import com.resilientmesh.gateway.client.OrderClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderGatewayController {

    private final OrderClient client;

    public OrderGatewayController(OrderClient client) {
        this.client = client;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getOrder(@PathVariable String id) {
        return client.getOrder(id);
    }
}
