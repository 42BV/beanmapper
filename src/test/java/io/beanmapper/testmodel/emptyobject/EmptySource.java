package io.beanmapper.testmodel.emptyobject;

import io.beanmapper.annotations.BeanProperty;

public class EmptySource {

    public Integer id;
    public String name;
    public Boolean bool;
    @BeanProperty(name = "nestedEmptyClass.name")
    public String emptyName;
    public NestedEmptySource nestedEmpty;
}
