package com.SyncMate.SyncMate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HealthService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, String> checkHealth() {
        Map<String, String> statusMap = new HashMap<>();

        statusMap.put("app", "UP");

        try {
            jdbcTemplate.execute("SELECT 1");
            statusMap.put("database", "UP");
        } catch (Exception e) {
            statusMap.put("database", "DOWN");
        }

        boolean overallUp = statusMap.values().stream().allMatch("UP"::equals);
        statusMap.put("status", overallUp ? "UP" : "DOWN");

        return statusMap;
    }
}
