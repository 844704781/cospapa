package com.watermelon.seimicrwaler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.watermelon.seimicrwaler.dao")
public class SeimicrwalerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeimicrwalerApplication.class, args);
    }

}
