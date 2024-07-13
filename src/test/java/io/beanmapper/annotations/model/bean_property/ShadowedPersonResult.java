package io.beanmapper.annotations.model.bean_property;

import io.beanmapper.annotations.BeanProperty;

public class ShadowedPersonResult {

    @BeanProperty("lastName")
    public String firstName;
    @BeanProperty("lastName")
    public String lastName;

    public ShadowedPersonResult(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @BeanProperty("lastName")
    public void setFirstName(String lastName) {
        this.lastName = lastName;
    }
}
