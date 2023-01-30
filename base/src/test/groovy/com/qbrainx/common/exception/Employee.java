package com.qbrainx.common.exception;

import com.qbrainx.common.validation.CustomNonNull;
import com.qbrainx.common.validation.CustomNotBlank;

public class Employee {

    @CustomNonNull(fieldCode = "EMP_NAME", fieldDefaultValue = "Name")
    private String name;

    @CustomNonNull(fieldCode = "EMP_AGE", fieldDefaultValue = "Age")
    private Integer age;

    @CustomNotBlank(fieldCode = "EMP_EMAIL", fieldDefaultValue = "email")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
