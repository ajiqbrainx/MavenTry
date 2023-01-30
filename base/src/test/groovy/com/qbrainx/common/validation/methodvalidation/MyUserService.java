package com.qbrainx.common.validation.methodvalidation;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Service
@Validated
public class MyUserService {

    public MyUser getUser(@NotBlank String uuid) {
        return new MyUser("John");
    }

    public void createUser(@Valid MyUser myUser) {
        System.out.println("Creating user");
    }
}
