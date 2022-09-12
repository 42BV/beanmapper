package io.beanmapper.testmodel.not_accessible.target_contains_nested_class;

import io.beanmapper.annotations.BeanProperty;

public class SourceWithPersonName {

    @BeanProperty(name = "person.fullName")
    public String name;

}
