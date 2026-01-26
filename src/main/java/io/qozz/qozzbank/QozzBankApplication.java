package io.qozz.qozzbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableResilientMethods
@SpringBootApplication
public class QozzBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(QozzBankApplication.class, args);
    }

}
