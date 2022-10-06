package io.beanmapper.testmodel.beanproperty;

import io.beanmapper.annotations.BeanProperty;

public class TargetBeanPropertyWithShadowing {

    private Integer age;

    @BeanProperty("age")
    public Integer age2;

    public void setAge(Integer age) {
        this.age = age;
    }

}
