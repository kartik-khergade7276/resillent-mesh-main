package com.resilientmesh.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DownstreamConfig {

    @Value("${downstreams.orders.base-urls}")
    private String baseUrls;

    @Bean
    public List<String> orderServiceBaseUrls() {
        return Arrays.stream(baseUrls.split(","))
                .map(String::trim)
                .toList();
    }
}
