package io.beanmapper.annotations.model.bean_property;

import io.beanmapper.annotations.BeanProperty;

public class PersonResult {

    @BeanProperty(value = "henk", targets = String.class)
    @BeanProperty(value = "fullName", targets = PersonResult.class)
    public String fullName;

}
