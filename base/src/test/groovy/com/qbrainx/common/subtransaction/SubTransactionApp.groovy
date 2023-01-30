package com.dijta.common.subtransaction

import com.dijta.common.EnableDijtaBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDijtaBase
public class SubTransactionApp {

    static void main(String[] args) {
        SpringApplication.run(SubTransactionApp.class, args)
    }
}
