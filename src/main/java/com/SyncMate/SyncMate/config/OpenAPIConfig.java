package com.SyncMate.SyncMate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI syncMateOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SyncMate API")
                        .description("API documentation for SyncMate application")
                        .version("v0.0.1")
                        .contact(new Contact()
                                .name("MohitSaini")
                                .email("mailtomohit2002@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}