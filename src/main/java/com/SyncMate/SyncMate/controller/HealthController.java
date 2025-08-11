package com.SyncMate.SyncMate.controller;

import com.SyncMate.SyncMate.services.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private HealthService healthService;

    @GetMapping
    public ResponseEntity<Map<String, String>> getHealthStatus() {
        return ResponseEntity.ok(healthService.checkHealth());
    }
}
