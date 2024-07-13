package io.beanmapper.annotations.model.bean_property;

import io.beanmapper.annotations.BeanProperty;

public class Person {

    private String name;

    public Person() {}

    public Person(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @BeanProperty(name = "fullName", targets = { PersonResult.class })
    public String getName() {
        return name;
    }
}
