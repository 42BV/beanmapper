package io.beanmapper.testmodel.beanAlias;

import io.beanmapper.annotations.BeanAlias;

public class NestedSourceWithAlias {

    public String property;
    @BeanAlias("nestedString")
    public String x;
    @BeanAlias("nestedInt")
    public int y;
}