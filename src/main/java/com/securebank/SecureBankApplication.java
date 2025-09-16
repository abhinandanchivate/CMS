package com.securebank;

import com.securebank.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class SecureBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureBankApplication.class, args);
    }
}
