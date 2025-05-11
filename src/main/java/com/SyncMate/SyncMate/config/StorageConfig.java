package com.SyncMate.SyncMate.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class StorageConfig {

    @Value("${gcp.storage.credentials.location}")
    private String credentialsPath;

    @Bean
    public Storage storage() throws IOException {
        // Load credentials from classpath or file system
        InputStream credentialsStream;
        if (credentialsPath.startsWith("classpath:")) {
            String resourcePath = credentialsPath.substring("classpath:".length());
            credentialsStream = new ClassPathResource(resourcePath).getInputStream();
        } else {
            credentialsStream = new FileInputStream(credentialsPath);
        }

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }
}