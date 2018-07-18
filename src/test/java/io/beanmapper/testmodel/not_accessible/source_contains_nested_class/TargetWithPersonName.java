package io.beanmapper.testmodel.not_accessible.source_contains_nested_class;

import io.beanmapper.annotations.BeanProperty;

public class TargetWithPersonName {

    @BeanProperty(name = "person.fullName")
    public String name;


}
