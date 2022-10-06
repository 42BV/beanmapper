package io.beanmapper.testmodel.beanproperty;

import io.beanmapper.annotations.BeanProperty;

public class TargetBeanPropertyWithShadowingNonPublicFieldWithoutSetter {

    private Integer age;

    @BeanProperty("age")
    public Integer age2;

}
