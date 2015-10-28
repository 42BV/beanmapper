package io.beanmapper.testmodel.constructNotMatching;

import io.beanmapper.annotations.BeanAlias;
import io.beanmapper.annotations.BeanProperty;

public class FlatConstructSource2 {

    public Long id;
    public String street;
    @BeanAlias("city")
    @BeanProperty(name = "nestedClass.city")
    public String city;
    @BeanAlias("country")
    @BeanProperty(name = "nestedClass.country")
    public String country;
}
