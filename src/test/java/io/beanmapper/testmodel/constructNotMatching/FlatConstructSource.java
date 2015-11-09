package io.beanmapper.testmodel.constructNotMatching;

import io.beanmapper.annotations.BeanAlias;

public class FlatConstructSource {

    public Long id;
    public String street;
    @BeanAlias("city")
    public String city;
    @BeanAlias("country")
    public String country;
}
