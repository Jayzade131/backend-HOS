package com.org.hosply360;

import com.org.hosply360.helper.SwaggerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties(SwaggerProperties.class)
@EnableScheduling
@EnableAsync
public class Hosply360Application {

    public static void main(String[] args) {
        SpringApplication.run(Hosply360Application.class, args);
    }

}
