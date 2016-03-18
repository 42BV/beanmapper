package io.beanmapper.dynclass.model;

import io.beanmapper.annotations.BeanProperty;

public class PersonDto {

    public Long id;

    private String name;

    public String street;

    @BeanProperty(name = "houseNumber")
    public String number;

    public String city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
