package com.sales.customer.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health/live")
    public Map<String, String> live() {
        return Map.of("status", "UP");
    }

    @GetMapping("/health/ready")
    public Map<String, String> ready() {
        return Map.of("status", "UP");
    }

    @GetMapping("/health/startup")
    public Map<String, String> startup() {
        return Map.of("status", "UP");
    }
}
