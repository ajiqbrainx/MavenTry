package com.qbrainx.common.validation.methodvalidation;

import javax.persistence.EntityManager;

public class UserRepository {

    private EntityManager entityManager;

    public User loadUser() {
        return new User("John Doe");
    }
}