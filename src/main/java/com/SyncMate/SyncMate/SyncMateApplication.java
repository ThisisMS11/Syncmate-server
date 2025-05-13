package com.SyncMate.SyncMate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class SyncMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncMateApplication.class, args);
    }

    @Bean
    public AbstractPlatformTransactionManager PlatformTransactionManager (MongoDatabaseFactory dbFactory){
        return new MongoTransactionManager(dbFactory);
    }

}
