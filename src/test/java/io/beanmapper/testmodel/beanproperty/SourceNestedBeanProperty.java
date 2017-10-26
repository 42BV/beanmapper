package io.beanmapper.testmodel.beanproperty;

import io.beanmapper.annotations.BeanProperty;

public class SourceNestedBeanProperty {

    @BeanProperty(name = "alpha.beta.gamma")
    public String value1;

    @BeanProperty(name = "alpha.beta.delta")
    public String value2;

}
