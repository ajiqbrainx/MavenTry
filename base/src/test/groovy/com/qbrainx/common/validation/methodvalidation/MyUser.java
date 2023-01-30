package com.qbrainx.common.validation.methodvalidation;

import javax.validation.constraints.NotBlank;

public class MyUser {

    @NotBlank
    private String name;

    public MyUser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}