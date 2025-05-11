package com.SyncMate.SyncMate.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ConfigDebugController {

    @Value("${springdoc.api-docs.path:unknown}")
    private String apiDocsPath;

    @Value("${springdoc.swagger-ui.path:unknown}")
    private String swaggerUIPath;

    @Value("${server.servlet.context-path:unknown}")
    private String contextPath;

    @GetMapping("/config-info")
    public ResponseEntity<Map<String, String>> getConfigInfo() {
        Map<String, String> configInfo = new HashMap<>();
        configInfo.put("apiDocsPath", apiDocsPath);
        configInfo.put("swaggerUIPath", swaggerUIPath);
        configInfo.put("contextPath", contextPath);
        configInfo.put("fullApiDocsUrl", contextPath + apiDocsPath);
        configInfo.put("fullSwaggerUIUrl", contextPath + swaggerUIPath);
        return ResponseEntity.ok(configInfo);
    }
}