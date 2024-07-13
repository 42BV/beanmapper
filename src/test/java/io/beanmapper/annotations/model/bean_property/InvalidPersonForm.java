package io.beanmapper.annotations.model.bean_property;

import io.beanmapper.annotations.BeanProperty;

public class InvalidPersonForm {

    private String firstName;
    private String lastName;

    public InvalidPersonForm(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @BeanProperty("name")
    @BeanProperty("fullName")
    public String getFirstName() {
        return firstName;
    }
}
