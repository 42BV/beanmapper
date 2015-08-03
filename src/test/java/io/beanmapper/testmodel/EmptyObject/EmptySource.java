package io.beanmapper.testmodel.EmptyObject;

import io.beanmapper.annotations.BeanProperty;

public class EmptySource {

    public Integer id;
    public String name;
    @BeanProperty(name = "nestedEmptyClass.name")
    public String emptyName;
}
