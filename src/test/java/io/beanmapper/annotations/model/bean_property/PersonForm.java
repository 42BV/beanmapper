package io.beanmapper.annotations.model.bean_property;

import io.beanmapper.annotations.BeanIgnore;
import io.beanmapper.annotations.BeanProperty;

public class PersonForm {

    public String firstName;
    public String lastName;
    @BeanIgnore
    @BeanProperty("ignored")
    public String ignored;

    public PersonForm(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @BeanProperty(name = "name")
    @BeanProperty(name = "name", targets = Person.class)
    @BeanProperty(name = "fullName", targets = PersonResult.class)
    public String getFullName() {
        return "%s %s".formatted(firstName, lastName);
    }
}
