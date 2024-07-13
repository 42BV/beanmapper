package io.beanmapper.annotations.model.bean_property;

import io.beanmapper.annotations.BeanProperty;

public class ComplexInvalidPersonForm {

    @BeanProperty("firstName")
    @BeanProperty(name = "firstName", targets = { ComplexPerson.class, ComplexPersonResult.class })
    @BeanProperty(name = "f_name", targets = { ComplexPerson.class, ComplexPersonResult.class })
    public String firstName;

    @BeanProperty(name = "lastName", targets = { ComplexPerson.class, ComplexPersonResult.class })
    @BeanProperty(name = "l_name", targets = { ComplexPerson.class, ComplexPersonResult.class })
    public String lastName;

    public ComplexInvalidPersonForm(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
