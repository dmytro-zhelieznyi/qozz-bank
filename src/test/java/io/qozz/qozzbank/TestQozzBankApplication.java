package io.qozz.qozzbank;

import org.springframework.boot.SpringApplication;

public class TestQozzBankApplication {

    static void main(String[] args) {
        SpringApplication.from(QozzBankApplication::main).run(args);
    }

}
