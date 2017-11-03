package io.beanmapper.testmodel.construct_not_matching;

import io.beanmapper.annotations.BeanAlias;

public class FlatConstructSource {

    public Long id;
    public String street;
    @BeanAlias("city")
    public String city;
    @BeanAlias("country")
    public String country;
}
