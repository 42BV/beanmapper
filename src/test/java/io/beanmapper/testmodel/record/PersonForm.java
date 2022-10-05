package io.beanmapper.testmodel.record;

import io.beanmapper.annotations.BeanAlias;

public class PersonForm {

    @BeanAlias("id")
    public int abc;
    public String name;

}
