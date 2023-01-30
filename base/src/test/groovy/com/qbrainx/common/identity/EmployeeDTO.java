package com.qbrainx.common.identity;

import com.qbrainx.common.validation.CustomNonNull;

/*
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter*/
public class EmployeeDTO extends BaseDTO {

    private Long sequence;
    @CustomNonNull(fieldCode = "EMP_NAME", fieldDefaultValue = "Name")
    private String name;
    private String lastName;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(final Long sequence) {
        this.sequence = sequence;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
}
