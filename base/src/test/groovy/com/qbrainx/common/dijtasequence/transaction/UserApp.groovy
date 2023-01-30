package com.dijta.common.dijtasequence.transaction

import com.dijta.common.EnableDijtaBase
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDijtaBase
public class UserApp {

    static void main(String[] args) {
        SpringApplication.run(UserApp.class, args)
    }

}
