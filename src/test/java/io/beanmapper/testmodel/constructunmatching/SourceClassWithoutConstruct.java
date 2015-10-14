package io.beanmapper.testmodel.constructunmatching;

import io.beanmapper.annotations.BeanProperty;

public class SourceClassWithoutConstruct {

    public Long id;
    public String street;
    @BeanProperty(name="nestedClass.city")
    public String city;
    @BeanProperty(name="nestedClass.country")
    public String country;

}
