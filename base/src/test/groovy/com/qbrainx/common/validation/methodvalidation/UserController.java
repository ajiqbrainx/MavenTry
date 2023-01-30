package com.qbrainx.common.validation.methodvalidation;

public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    public User getUser() {
        return this.service.getUser();
    }

}